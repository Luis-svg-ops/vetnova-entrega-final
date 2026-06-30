package cl.vetnova.fichaclinica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.fichaclinica.client.AuthClient;
import cl.vetnova.fichaclinica.dto.MascotaDesactivacionResponse;
import cl.vetnova.fichaclinica.dto.MascotaResponse;
import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.model.Mascota;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.MascotaRepository;

public class MascotaServiceTest {

    // @Mock: dobles de prueba — reemplazan los beans reales sin levantar contexto Spring
    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private FichaClinicaRepository fichaClinicaRepository;  // necesario porque crear() guarda la FichaClinica

    @Mock
    private AuthClient authClient;  // simula llamadas HTTP a vetnova_auth (8081) para obtener nombres

    // @InjectMocks: crea MascotaService real e inyecta los tres mocks anteriores en sus @Autowired
    @InjectMocks
    private MascotaService mascotaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper: construye mascota con los campos dados (permite probar cada campo nulo por separado)
    private Mascota mascota(Long clienteId, String nombre, String especie) {
        Mascota m = new Mascota();
        m.setClienteId(clienteId);
        m.setNombre(nombre);
        m.setEspecie(especie);
        return m;
    }

    // Helper: mascota mínima válida para los tests que no prueban validaciones de campos
    private Mascota valida() {
        return mascota(1L, "Rex", "PERRO");
    }

    // Helper: configura save() en ambos repositorios para devolver la misma entidad que reciben
    private void guarda() {
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ═══════════════════════════════════════════════════════
    //  CREAR MASCOTA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: el clienteId es obligatorio — sin él no se puede saber a qué cliente pertenece la mascota.
     *
     * GIVEN: mascota con clienteId = null
     * WHEN:  crear()
     * THEN:  BusinessRuleException "El clienteId es obligatorio"
     *        (la validación local falla antes de cualquier llamada a la BD)
     */
    @Test
    void testCrearClienteIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(null, "Rex", "PERRO")));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    /**
     * Regla: el nombre es obligatorio para identificar a la mascota.
     *
     * GIVEN: mascota con nombre = null
     * WHEN:  crear()
     * THEN:  BusinessRuleException "El nombre es obligatorio"
     */
    @Test
    void testCrearNombreNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, null, "PERRO")));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    /**
     * Regla: el nombre no puede ser una cadena vacía o solo espacios.
     * Por qué este caso: nombre="" pasa el null-check pero falla el isBlank() — dos validaciones distintas.
     *
     * GIVEN: mascota con nombre = "" (vacío)
     * WHEN:  crear()
     * THEN:  BusinessRuleException "El nombre no puede estar vacío"
     */
    @Test
    void testCrearNombreVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, "", "PERRO")));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    /**
     * Regla: la especie es obligatoria para clasificar correctamente a la mascota.
     *
     * GIVEN: mascota con especie = null
     * WHEN:  crear()
     * THEN:  BusinessRuleException "La especie es obligatoria"
     */
    @Test
    void testCrearEspecieNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, "Rex", null)));
        assertEquals("La especie es obligatoria", ex.getMessage());
    }

    /**
     * Regla: la especie tampoco puede ser cadena vacía.
     *
     * GIVEN: mascota con especie = "" (vacío)
     * WHEN:  crear()
     * THEN:  BusinessRuleException "La especie no puede estar vacía"
     */
    @Test
    void testCrearEspecieVacia() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, "Rex", "")));
        assertEquals("La especie no puede estar vacía", ex.getMessage());
    }

    /**
     * Regla: una mascota no puede nacer en el futuro — la fecha de nacimiento debe ser pasada o hoy.
     * Por qué este caso: mañana es el valor mínimo que viola la regla.
     *
     * GIVEN: mascota con fechaNacimiento = mañana
     * WHEN:  crear()
     * THEN:  BusinessRuleException "La fecha de nacimiento no puede ser futura"
     */
    @Test
    void testCrearFechaNacimientoFutura() {
        Mascota m = valida();
        m.setFechaNacimiento(LocalDate.now().plusDays(1));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> mascotaService.crear(m));
        assertEquals("La fecha de nacimiento no puede ser futura", ex.getMessage());
    }

    /**
     * Regla: el peso no puede ser cero ni negativo — un animal siempre tiene masa positiva.
     * Por qué este caso: 0.0 es el límite exacto que viola la regla (mayor estricto a 0).
     *
     * GIVEN: mascota con peso = 0.0
     * WHEN:  crear()
     * THEN:  BusinessRuleException "El peso debe ser mayor a 0"
     */
    @Test
    void testCrearPesoNoPositivo() {
        Mascota m = valida();
        m.setPeso(0.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> mascotaService.crear(m));
        assertEquals("El peso debe ser mayor a 0", ex.getMessage());
    }

    /**
     * Regla: el microchip es único en todo el sistema — dos mascotas no pueden compartir el mismo chip.
     * Por qué este caso: simula que "ABC123" ya existe en BD → debe rechazar el duplicado.
     *
     * GIVEN: mascota con microchip="ABC123"
     *        Mock: mascotaRepository.existsByMicrochip("ABC123") → true (ya existe)
     * WHEN:  crear()
     * THEN:  ConflictException "Ya existe una mascota con ese microchip"
     *        (409 Conflict, no 400 — es un conflicto de unicidad, no dato inválido)
     */
    @Test
    void testCrearMicrochipDuplicado() {
        Mascota m = valida();
        m.setMicrochip("ABC123");
        when(mascotaRepository.existsByMicrochip("ABC123")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> mascotaService.crear(m));
        assertEquals("Ya existe una mascota con ese microchip", ex.getMessage());
    }

    /**
     * Regla: al crear una mascota, se crea automáticamente su ficha clínica vacía.
     *        La mascota nace con activo=true.
     * Por qué este caso: verifica el efecto secundario más importante de crear() — la ficha automática.
     *
     * GIVEN: mascota con todos los opcionales (fechaNacimiento, peso, microchip) para cobertura completa
     *        Mock: existsByMicrochip("XYZ999") → false (chip libre)
     *        Mock: mascotaRepository.save y fichaClinicaRepository.save devuelven lo que reciben
     * WHEN:  crear()
     * THEN:  mascota.activo = true
     *        verify(fichaClinicaRepository).save() — confirma que la ficha se guardó en la misma transacción
     */
    @Test
    void testCrearCasoFelizCreaFicha() {
        guarda();
        Mascota m = valida();
        m.setFechaNacimiento(LocalDate.of(2020, 1, 1));
        m.setPeso(12.5);
        m.setMicrochip("XYZ999");
        Mascota creada = mascotaService.crear(m);
        assertEquals(true, creada.getActivo());
        // El punto clave: fichaClinicaRepository.save() debe haberse llamado exactamente una vez
        verify(fichaClinicaRepository).save(any(FichaClinica.class));
    }

    /**
     * Regla: clienteId, nombre y especie son los únicos campos obligatorios — el resto es opcional.
     *
     * GIVEN: mascota con solo los campos mínimos (sin peso, sin microchip, sin fechaNacimiento)
     * WHEN:  crear()
     * THEN:  se guarda correctamente con activo=true — los opcionales no rompen la creación
     */
    @Test
    void testCrearSinOpcionalesEsValido() {
        guarda();
        Mascota creada = mascotaService.crear(valida());
        assertEquals(true, creada.getActivo());
    }

    // ═══════════════════════════════════════════════════════
    //  ACTUALIZAR MASCOTA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: el peso tampoco puede ser negativo al actualizar.
     *
     * GIVEN: mascota id=1 existe; datos con peso=-3.0
     *        Mock: findById(1L) → mascota válida
     * WHEN:  actualizar(1L, datos)
     * THEN:  BusinessRuleException "El peso debe ser mayor a 0"
     */
    @Test
    void testActualizarPesoInvalido() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        Mascota datos = valida();
        datos.setPeso(-3.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.actualizar(1L, datos));
        assertEquals("El peso debe ser mayor a 0", ex.getMessage());
    }

    /**
     * Regla: la actualización aplica los nuevos datos y los persiste.
     *
     * GIVEN: mascota id=1 existe con raza=null; datos con peso=12.5 y raza="Golden"
     *        Mock: findById → mascota existente; save → devuelve lo que recibe
     * WHEN:  actualizar(1L, datos)
     * THEN:  raza = "Golden" — los nuevos valores se aplicaron correctamente
     */
    @Test
    void testActualizarCasoFeliz() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));
        Mascota datos = valida();
        datos.setPeso(12.5);
        datos.setRaza("Golden");
        assertEquals("Golden", mascotaService.actualizar(1L, datos).getRaza());
    }

    /**
     * Regla: si peso viene null al actualizar, se omite la validación — el campo es opcional.
     *
     * GIVEN: datos con peso=null y raza="Beagle"
     * WHEN:  actualizar(1L, datos)
     * THEN:  raza="Beagle" — la actualización pasa sin error de peso
     */
    @Test
    void testActualizarSinPesoEsValido() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));
        Mascota datos = valida();
        datos.setRaza("Beagle");
        assertEquals("Beagle", mascotaService.actualizar(1L, datos).getRaza());
    }

    // ═══════════════════════════════════════════════════════
    //  DESACTIVAR MASCOTA (soft delete)
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: desactivar pone activo=false — la mascota deja de aparecer en listados activos
     *        pero sus evoluciones, recetas y ficha clínica se preservan en BD.
     *
     * GIVEN: mascota activa (activo=true)
     *        Mock: findById → mascota activa; save → devuelve lo que recibe
     * WHEN:  desactivar(1L)
     * THEN:  mascota.activo = false, mensaje = "Mascota desactivada"
     */
    @Test
    void testDesactivarCasoFeliz() {
        Mascota m = valida();
        m.setActivo(true);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(m));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));
        MascotaDesactivacionResponse resp = mascotaService.desactivar(1L);
        assertEquals(false, resp.getMascota().getActivo());
        assertEquals("Mascota desactivada", resp.getMensaje());
    }

    /**
     * Regla: desactivar es idempotente — si ya estaba inactiva, no falla ni guarda de nuevo.
     * Por qué este caso: llamar dos veces a desactivar no debe generar error ni escrituras innecesarias.
     *
     * GIVEN: mascota ya inactiva (activo=false)
     *        Mock: findById → mascota inactiva
     * WHEN:  desactivar(1L)
     * THEN:  mensaje = "La mascota ya estaba inactiva"
     *        verify que mascotaRepository.save() NUNCA fue llamado — no hay escritura en BD
     */
    @Test
    void testDesactivarYaInactivaEsIdempotente() {
        Mascota m = valida();
        m.setActivo(false);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(m));
        MascotaDesactivacionResponse resp = mascotaService.desactivar(1L);
        assertEquals("La mascota ya estaba inactiva", resp.getMensaje());
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    // ═══════════════════════════════════════════════════════
    //  LISTAR / OBTENER
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: listar() delega al repositorio sin transformaciones.
     *
     * GIVEN: repositorio contiene 1 mascota
     * WHEN:  listar()
     * THEN:  lista de tamaño 1
     */
    @Test
    void testListar() {
        when(mascotaRepository.findAll()).thenReturn(List.of(new Mascota()));
        assertEquals(1, mascotaService.listar().size());
    }

    /**
     * Regla: buscar una mascota inexistente lanza 404, no devuelve null.
     *
     * GIVEN: findById(99L) → Optional.empty()
     * WHEN:  obtenerPorId(99L)
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> mascotaService.obtenerPorId(99L));
    }

    /**
     * Regla: buscar una mascota existente devuelve la entidad correcta.
     *
     * GIVEN: findById(1L) → mascota "Rex"
     * WHEN:  obtenerPorId(1L)
     * THEN:  nombre = "Rex"
     */
    @Test
    void testObtenerPorIdExistenteDevuelveLaMascota() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        assertEquals("Rex", mascotaService.obtenerPorId(1L).getNombre());
    }

    /**
     * Regla: listarConCliente() enriquece cada mascota con el nombre del propietario desde auth.
     *
     * GIVEN: repositorio tiene 1 mascota (clienteId=1)
     *        Mock: authClient.obtenerNombreCliente(1L) → "Luis Hernández"
     * WHEN:  listarConCliente()
     * THEN:  lista de tamaño 1, nombreCliente = "Luis Hernández"
     *        Confirma que authClient fue llamado con el clienteId correcto
     */
    @Test
    void testListarConClienteRetornaNombreCliente() {
        when(mascotaRepository.findAll()).thenReturn(List.of(valida()));
        when(authClient.obtenerNombreCliente(1L)).thenReturn("Luis Hernández");
        List<MascotaResponse> lista = mascotaService.listarConCliente();
        assertEquals(1, lista.size());
        assertEquals("Luis Hernández", lista.get(0).getNombreCliente());
    }

    /**
     * Regla: si auth devuelve null (caído o sin datos), listarConCliente() sigue funcionando
     *        con nombreCliente=null — degradación SUAVE.
     * Por qué este caso: verifica que una respuesta null de auth no provoca NullPointerException.
     *
     * GIVEN: authClient.obtenerNombreCliente(1L) → null (auth caído o cliente sin nombre)
     * WHEN:  listarConCliente()
     * THEN:  lista retornada, nombreCliente = null — el endpoint no falla
     */
    @Test
    void testListarConClienteAuthCaidoRetornaNombreNull() {
        when(mascotaRepository.findAll()).thenReturn(List.of(valida()));
        when(authClient.obtenerNombreCliente(1L)).thenReturn(null);
        assertNull(mascotaService.listarConCliente().get(0).getNombreCliente());
    }

    /**
     * Regla: obtenerPorIdConCliente() busca la mascota por id y enriquece con el nombre del cliente.
     *
     * GIVEN: findById(1L) → mascota "Rex" (clienteId=1)
     *        Mock: authClient.obtenerNombreCliente(1L) → "Luis Hernández"
     * WHEN:  obtenerPorIdConCliente(1L)
     * THEN:  nombre="Rex", nombreCliente="Luis Hernández" — ambos campos correctos en el DTO
     */
    @Test
    void testObtenerPorIdConClienteRetornaNombreCliente() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        when(authClient.obtenerNombreCliente(1L)).thenReturn("Luis Hernández");
        MascotaResponse resp = mascotaService.obtenerPorIdConCliente(1L);
        assertEquals("Rex", resp.getNombre());
        assertEquals("Luis Hernández", resp.getNombreCliente());
    }
}
