package cl.vetnova.agenda.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.agenda.client.AuthClient;
import cl.vetnova.agenda.client.FichaClient;
import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.Cita;
import cl.vetnova.agenda.repository.CitaRepository;

public class CitaServiceTest {

    // Fecha fija en el año 2030: siempre futura, evita que el test falle si corre en una fecha real
    private static final LocalDateTime FUTURO = LocalDateTime.of(2030, 7, 1, 10, 0);

    // @Mock crea dobles de prueba: reemplazan a los beans reales de Spring sin levantar el contexto
    @Mock private CitaRepository citaRepository;
    @Mock private RecordatorioGenerador recordatorioGenerador;
    @Mock private AuthClient authClient;      // simula llamadas HTTP a vetnova_auth (8081)
    @Mock private FichaClient fichaClient;    // simula llamadas HTTP a vetnova_ficha (8087)

    // @InjectMocks crea CitaService real e inyecta todos los @Mock anteriores en sus campos @Autowired
    @InjectMocks private CitaService citaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Comportamiento por defecto: cliente y mascota existen → las validaciones duras pasan sin error.
        // Si un test necesita que fallen, lo sobreescribe individualmente con doThrow().
        doNothing().when(authClient).verificarCliente(any());
        doNothing().when(fichaClient).verificarMascota(any());
    }

    // Helper: construye una cita con todos los campos obligatorios válidos
    private Cita citaValida() {
        Cita c = new Cita();
        c.setClienteId(2L);
        c.setVeterinarioId(4L);
        c.setServicioId(3L);
        c.setSucursal("SANTIAGO");
        c.setFechaHora(FUTURO);
        c.setDuracionMinutos(30);
        return c;
    }

    // Helper: cita ya confirmada que ocupa el horario del veterinario (usada para probar solapamiento)
    private Cita citaConfirmada(LocalDateTime fecha, Integer duracion) {
        Cita c = new Cita();
        c.setVeterinarioId(4L);
        c.setEstado("confirmada");
        c.setFechaHora(fecha);
        c.setDuracionMinutos(duracion);
        return c;
    }

    // Helper: cita válida con id=1 en el estado dado (usada para pruebas de transición de estados)
    private Cita citaEnEstado(String estado) {
        Cita c = citaValida();
        c.setId(1L);
        c.setEstado(estado);
        return c;
    }

    // Helper: configura el mock del repositorio para devolver la misma cita que recibe (simula save exitoso)
    private void guarda() {
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ═══════════════════════════════════════════════════════
    //  CREAR CITA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: clienteId es obligatorio para poder identificar al dueño de la mascota.
     * Por qué este caso: null es el valor más básico de campo faltante.
     *
     * GIVEN: cita sin clienteId (null)
     * WHEN:  se llama a citaService.crear()
     * THEN:  lanza BusinessRuleException con mensaje exacto
     *        (no se hacen llamadas remotas: la validación local falla primero)
     */
    @Test
    void testCrearClienteNull() {
        Cita c = citaValida();
        c.setClienteId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    /**
     * Regla: veterinarioId es obligatorio para asignar la cita a un profesional.
     *
     * GIVEN: cita sin veterinarioId
     * WHEN:  crear()
     * THEN:  BusinessRuleException — nunca llega a verificar disponibilidad
     */
    @Test
    void testCrearVeterinarioNull() {
        Cita c = citaValida();
        c.setVeterinarioId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    /**
     * Regla: servicioId es obligatorio para saber qué tipo de atención se agenda.
     *
     * GIVEN: cita sin servicioId
     * WHEN:  crear()
     * THEN:  BusinessRuleException
     */
    @Test
    void testCrearServicioNull() {
        Cita c = citaValida();
        c.setServicioId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("El servicioId es obligatorio", ex.getMessage());
    }

    /**
     * Regla: la fecha y hora son obligatorias; sin ellas no existe la cita.
     *
     * GIVEN: cita con fechaHora = null
     * WHEN:  crear()
     * THEN:  BusinessRuleException — la validación de fecha pasada no llega a ejecutarse
     */
    @Test
    void testCrearFechaNull() {
        Cita c = citaValida();
        c.setFechaHora(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("La fecha y hora son obligatorias", ex.getMessage());
    }

    /**
     * Regla: no se pueden agendar citas en el pasado — una cita pasada no puede atenderse.
     * Por qué este caso: 2020 es inequívocamente pasado; confirma que la regla temporal funciona.
     *
     * GIVEN: cita con fecha en 2020 (pasado)
     * WHEN:  crear()
     * THEN:  BusinessRuleException "La fecha y hora deben ser futuras"
     */
    @Test
    void testCrearFechaEnPasado() {
        Cita c = citaValida();
        c.setFechaHora(LocalDateTime.of(2020, 1, 1, 10, 0));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("La fecha y hora deben ser futuras", ex.getMessage());
    }

    /**
     * Regla: la sucursal es obligatoria para saber en qué sede se atiende.
     *
     * GIVEN: cita con sucursal = null
     * WHEN:  crear()
     * THEN:  BusinessRuleException
     */
    @Test
    void testCrearSucursalNull() {
        Cita c = citaValida();
        c.setSucursal(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    /**
     * Regla: solo se aceptan las sucursales del sistema (CHILLAN, LOS_ANGELES, TALCA, SANTIAGO).
     * Por qué este caso: "FANTASMA" no existe → confirma que la validación rechaza valores arbitrarios.
     *
     * GIVEN: cita con sucursal = "FANTASMA"
     * WHEN:  crear()
     * THEN:  ResourceNotFoundException "Sucursal no encontrada"
     *        (mismo tipo de error que un recurso de BD inexistente)
     */
    @Test
    void testCrearSucursalInexistente() {
        Cita c = citaValida();
        c.setSucursal("FANTASMA");
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> citaService.crear(c));
        assertEquals("Sucursal no encontrada", ex.getMessage());
    }

    /**
     * Regla: un veterinario no puede tener dos citas solapadas.
     * Por qué este caso: la cita nueva (+15 min) cae dentro de los 30 min de la existente → solapamiento.
     *
     * GIVEN: veterinario 4 tiene cita confirmada a FUTURO (30 min de duración por defecto)
     *        Mock: findByVeterinarioIdAndEstado devuelve esa cita existente
     * WHEN:  crear() cita nueva a FUTURO+15min para el mismo veterinario
     * THEN:  ConflictException "El veterinario no está disponible en ese horario"
     */
    @Test
    void testCrearVeterinarioNoDisponiblePorSolapamiento() {
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO, null)));
        Cita nueva = citaValida();
        nueva.setFechaHora(FUTURO.plusMinutes(15));
        ConflictException ex = assertThrows(ConflictException.class, () -> citaService.crear(nueva));
        assertEquals("El veterinario no está disponible en ese horario", ex.getMessage());
    }

    /**
     * Regla: si la cita existente terminó ANTES de que empiece la nueva, no hay solapamiento.
     * Por qué este caso: cita previa termina a FUTURO-1h30; la nueva empieza a FUTURO → sin colisión.
     *
     * GIVEN: veterinario tiene cita confirmada 2h antes (duracion=30 min, termina 1h30 antes)
     *        Mock: save devuelve la misma cita que recibe
     * WHEN:  crear() cita nueva a FUTURO
     * THEN:  se guarda con estado "pendiente" — el solapamiento no se detecta
     */
    @Test
    void testCrearSinSolapamientoExistenteAnterior() {
        guarda();
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO.minusHours(2), 30)));
        assertEquals("pendiente", citaService.crear(citaValida()).getEstado());
    }

    /**
     * Regla: si la cita existente empieza DESPUÉS de que termina la nueva, tampoco hay solapamiento.
     * Por qué este caso: caso simétrico al anterior — cita posterior no bloquea la nueva.
     *
     * GIVEN: veterinario tiene cita 2h después (sin colisión con la nueva de 30 min)
     * WHEN:  crear()
     * THEN:  estado "pendiente" — guardado exitoso
     */
    @Test
    void testCrearSinSolapamientoExistentePosterior() {
        guarda();
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO.plusHours(2), 30)));
        assertEquals("pendiente", citaService.crear(citaValida()).getEstado());
    }

    /**
     * Regla: el box físico tampoco puede estar ocupado al mismo tiempo por dos citas.
     *
     * GIVEN: box 5 está ocupado a FUTURO por 60 min
     *        Mock: findByBoxIdAndEstado("pendiente") devuelve esa cita ocupada
     *        Mock: findByVeterinarioIdAndEstado devuelve lista vacía (veterinario libre)
     * WHEN:  crear() cita nueva con boxId=5 a FUTURO
     * THEN:  ConflictException — el box bloquea aunque el veterinario esté libre
     */
    @Test
    void testCrearBoxOcupadoLanzaConflict() {
        when(citaRepository.findByVeterinarioIdAndEstado(any(), any())).thenReturn(List.of());
        Cita ocupada = new Cita();
        ocupada.setFechaHora(FUTURO);
        ocupada.setDuracionMinutos(60);
        when(citaRepository.findByBoxIdAndEstado(5L, "pendiente")).thenReturn(List.of(ocupada));
        when(citaRepository.findByBoxIdAndEstado(5L, "confirmada")).thenReturn(List.of());

        Cita nueva = citaValida();
        nueva.setBoxId(5L);

        assertThrows(ConflictException.class, () -> citaService.crear(nueva));
    }

    /**
     * Regla: caso feliz con box libre y veterinario libre.
     *
     * GIVEN: no hay colisiones de veterinario ni de box
     *        Mock: todos findBy devuelven lista vacía
     * WHEN:  crear() con boxId=5
     * THEN:  estado "pendiente" — guardado exitoso con box asignado
     */
    @Test
    void testCrearConBoxDisponibleCasoFeliz() {
        guarda();
        when(citaRepository.findByVeterinarioIdAndEstado(any(), any())).thenReturn(List.of());
        when(citaRepository.findByBoxIdAndEstado(any(), any())).thenReturn(List.of());

        Cita nueva = citaValida();
        nueva.setBoxId(5L);

        assertEquals("pendiente", citaService.crear(nueva).getEstado());
    }

    /**
     * Regla: al crear, la cita queda en PENDIENTE, con fechaCreacion seteada y recordatorio generado.
     * Por qué este caso: duracionMinutos=null prueba que el servicio usa el valor por defecto (30 min).
     *
     * GIVEN: sin citas previas del veterinario; duracionMinutos=null
     *        Mock: save devuelve la cita; recordatorioGenerador.generarParaCita no lanza error
     * WHEN:  crear()
     * THEN:  estado="pendiente", fechaCreacion≠null, se llama a recordatorioGenerador.generarParaCita
     */
    @Test
    void testCrearCasoFelizSinCitasPrevias() {
        guarda();
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada")).thenReturn(List.of());
        Cita c = citaValida();
        c.setDuracionMinutos(null);
        Cita creada = citaService.crear(c);
        assertEquals("pendiente", creada.getEstado());
        assertNotNull(creada.getFechaCreacion());
        // Verifica que el recordatorio EMAIL se genera automáticamente al crear la cita
        verify(recordatorioGenerador).generarParaCita(creada);
    }

    // ═══════════════════════════════════════════════════════
    //  CONFIRMAR CITA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: no se puede confirmar una cita ya cancelada — la cancelación es irreversible.
     *
     * GIVEN: cita en estado "cancelada"
     *        Mock: findById(1L) devuelve esa cita
     * WHEN:  confirmar(1L)
     * THEN:  BusinessRuleException "No se puede confirmar cita cancelada"
     */
    @Test
    void testConfirmarCanceladaEsInvalido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("cancelada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.confirmar(1L));
        assertEquals("No se puede confirmar cita cancelada", ex.getMessage());
    }

    /**
     * Regla: una cita pendiente puede confirmarse — avance válido en la máquina de estados.
     *
     * GIVEN: cita en estado "pendiente"
     * WHEN:  confirmar(1L)
     * THEN:  estado = "confirmada"
     */
    @Test
    void testConfirmarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        assertEquals("confirmada", citaService.confirmar(1L).getEstado());
    }

    /**
     * Regla: operar sobre una cita inexistente lanza 404.
     *
     * GIVEN: findById(99L) devuelve Optional.empty()
     * WHEN:  confirmar(99L)
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testConfirmarCitaInexistenteLanzaNotFound() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> citaService.confirmar(99L));
    }

    // ═══════════════════════════════════════════════════════
    //  INICIAR CITA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: solo se puede iniciar una cita que esté CONFIRMADA — la máquina de estados es estricta.
     * Por qué este caso: desde "pendiente" (sin confirmar) no se puede saltar directamente a EN_CURSO.
     *
     * GIVEN: cita en estado "pendiente"
     * WHEN:  iniciar(1L)
     * THEN:  BusinessRuleException "Debe estar confirmada antes de iniciarse"
     */
    @Test
    void testIniciarDesdePendienteEsInvalido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.iniciar(1L));
        assertEquals("Debe estar confirmada antes de iniciarse", ex.getMessage());
    }

    /**
     * Regla: CONFIRMADA → EN_CURSO es la transición correcta.
     *
     * GIVEN: cita en estado "confirmada"
     * WHEN:  iniciar(1L)
     * THEN:  estado = "en curso"
     */
    @Test
    void testIniciarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        assertEquals("en curso", citaService.iniciar(1L).getEstado());
    }

    // ═══════════════════════════════════════════════════════
    //  COMPLETAR CITA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: no se puede completar una cita que no está EN_CURSO.
     * Por qué este caso: desde "pendiente" (dos pasos antes) la transición directa no es válida.
     *
     * GIVEN: cita en estado "pendiente"
     * WHEN:  completar(1L)
     * THEN:  BusinessRuleException "Debe estar en curso antes de completarse"
     */
    @Test
    void testCompletarDesdePendienteEsInvalido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.completar(1L));
        assertEquals("Debe estar en curso antes de completarse", ex.getMessage());
    }

    /**
     * Regla: EN_CURSO → COMPLETADA es la transición final exitosa.
     *
     * GIVEN: cita en estado "en curso"
     * WHEN:  completar(1L)
     * THEN:  estado = "completada"
     */
    @Test
    void testCompletarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("en curso")));
        assertEquals("completada", citaService.completar(1L).getEstado());
    }

    // ═══════════════════════════════════════════════════════
    //  CANCELAR CITA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: una cita COMPLETADA no puede cancelarse — la atención ya ocurrió, no se puede borrar.
     *
     * GIVEN: cita en estado "completada"
     * WHEN:  cancelar(1L, "x")
     * THEN:  BusinessRuleException "No se puede cancelar cita completada"
     */
    @Test
    void testCancelarCompletadaEsImpedido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("completada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.cancelar(1L, "x"));
        assertEquals("No se puede cancelar cita completada", ex.getMessage());
    }

    /**
     * Regla: el motivo de cancelación es obligatorio para trazabilidad.
     *
     * GIVEN: cita en estado "pendiente", motivo = null
     * WHEN:  cancelar(1L, null)
     * THEN:  BusinessRuleException "El motivo es obligatorio"
     */
    @Test
    void testCancelarMotivoNull() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.cancelar(1L, null));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    /**
     * Regla: cancelar persiste el motivo y cancela los recordatorios pendientes automáticamente.
     *
     * GIVEN: cita en estado "confirmada", motivo = "Emergencia"
     *        Mock: save devuelve la cita modificada
     * WHEN:  cancelar(1L, "Emergencia")
     * THEN:  estado="cancelada", motivoCancelacion="Emergencia"
     *        se llama a recordatorioGenerador.cancelarPorCita — los avisos al cliente se anulan
     */
    @Test
    void testCancelarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        Cita cancelada = citaService.cancelar(1L, "Emergencia");
        assertEquals("cancelada", cancelada.getEstado());
        assertEquals("Emergencia", cancelada.getMotivoCancelacion());
        verify(recordatorioGenerador).cancelarPorCita(cancelada.getId());
    }

    // ═══════════════════════════════════════════════════════
    //  LISTAR / OBTENER
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: listar() delega directamente al repositorio sin filtros.
     *
     * GIVEN: repositorio contiene 1 cita
     * WHEN:  listar()
     * THEN:  retorna lista de tamaño 1
     */
    @Test
    void testListar() {
        when(citaRepository.findAll()).thenReturn(List.of(new Cita()));
        assertEquals(1, citaService.listar().size());
    }

    /**
     * Regla: obtener una cita que no existe lanza 404, no devuelve null.
     *
     * GIVEN: findById(99L) devuelve Optional.empty()
     * WHEN:  obtenerPorId(99L)
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> citaService.obtenerPorId(99L));
    }

    // ═══════════════════════════════════════════════════════
    //  REPROGRAMAR
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: caso feliz — se actualiza fecha y duración correctamente.
     *
     * GIVEN: cita pendiente, nueva fecha = FUTURO+1 día, nueva duración = 45 min
     * WHEN:  reprogramar(1L, nuevaFecha, 45)
     * THEN:  fechaHora y duracionMinutos actualizados
     */
    @Test
    void testReprogramarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        Cita result = citaService.reprogramar(1L, FUTURO.plusDays(1), 45);
        assertEquals(FUTURO.plusDays(1), result.getFechaHora());
        assertEquals(45, result.getDuracionMinutos());
    }

    /**
     * Regla: la nueva fecha es obligatoria al reprogramar.
     *
     * GIVEN: cita pendiente, nuevaFecha = null
     * WHEN:  reprogramar(1L, null, null)
     * THEN:  BusinessRuleException "La nueva fecha y hora son obligatorias"
     */
    @Test
    void testReprogramarFechaNullLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, null, null));
        assertEquals("La nueva fecha y hora son obligatorias", ex.getMessage());
    }

    /**
     * Regla: tampoco se puede reprogramar a una fecha pasada.
     *
     * GIVEN: cita pendiente, nuevaFecha = 2020 (pasado)
     * WHEN:  reprogramar(1L, pasado, null)
     * THEN:  BusinessRuleException "La nueva fecha y hora deben ser futuras"
     */
    @Test
    void testReprogramarFechaPasadaLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, LocalDateTime.of(2020, 1, 1, 10, 0), null));
        assertEquals("La nueva fecha y hora deben ser futuras", ex.getMessage());
    }

    /**
     * Regla: una cita COMPLETADA no puede reprogramarse — su historia ya está escrita.
     *
     * GIVEN: cita completada
     * WHEN:  reprogramar()
     * THEN:  BusinessRuleException con "completada" en el mensaje
     */
    @Test
    void testReprogramarCitaCompletadaLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("completada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, FUTURO.plusDays(1), null));
        assertTrue(ex.getMessage().contains("completada"));
    }

    /**
     * Regla: una cita CANCELADA tampoco puede reprogramarse.
     *
     * GIVEN: cita cancelada
     * WHEN:  reprogramar()
     * THEN:  BusinessRuleException con "cancelada" en el mensaje
     */
    @Test
    void testReprogramarCitaCanceladaLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("cancelada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, FUTURO.plusDays(1), null));
        assertTrue(ex.getMessage().contains("cancelada"));
    }

    /**
     * Regla: reprogramar con solapamiento lanza ConflictException.
     *
     * GIVEN: veterinario tiene cita confirmada en FUTURO+1d con 60 min
     *        Mock: findByVeterinarioIdAndEstado devuelve esa cita
     * WHEN:  reprogramar a FUTURO+1d (solapamiento exacto)
     * THEN:  ConflictException
     */
    @Test
    void testReprogramarConSolapamientoLanzaConflict() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO.plusDays(1), 60)));
        assertThrows(ConflictException.class,
                () -> citaService.reprogramar(1L, FUTURO.plusDays(1), 30));
    }

    /**
     * Regla: si no se pasa nueva duración, se conserva la duración original de la cita.
     * Por qué este caso: evita que una reprogramación sin duración resetee la duración a null.
     *
     * GIVEN: cita con duracionMinutos=50, nuevaDuracion=null
     * WHEN:  reprogramar(1L, nuevaFecha, null)
     * THEN:  duracionMinutos sigue siendo 50
     */
    @Test
    void testReprogramarSinDuracionConservaDuracionOriginal() {
        Cita original = citaEnEstado("pendiente");
        original.setDuracionMinutos(50);
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(original));
        Cita result = citaService.reprogramar(1L, FUTURO.plusDays(2), null);
        assertEquals(50, result.getDuracionMinutos());
    }

    // ═══════════════════════════════════════════════════════
    //  toResponse / degradación suave / listarConNombres
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: toResponse enriquece la cita con los nombres reales desde auth y ficha.
     *
     * GIVEN: authClient.obtenerNombre(2L) → "Juan Pérez" (cliente)
     *        authClient.obtenerNombre(4L) → "Dra. López" (veterinario)
     *        fichaClient.obtenerNombreMascota(1L) → "Firulais"
     * WHEN:  toResponse(cita)
     * THEN:  CitaResponse tiene los tres nombres correctamente asignados
     */
    @Test
    void testToResponseEnriqueceCamposNombre() {
        when(authClient.obtenerNombre(2L)).thenReturn("Juan Pérez");
        when(authClient.obtenerNombre(4L)).thenReturn("Dra. López");
        when(fichaClient.obtenerNombreMascota(1L)).thenReturn("Firulais");

        Cita c = citaEnEstado("pendiente");
        c.setMascotaId(1L);

        cl.vetnova.agenda.dto.CitaResponse resp = citaService.toResponse(c);

        assertEquals("Juan Pérez", resp.getNombreCliente());
        assertEquals("Firulais", resp.getNombreMascota());
        assertEquals("Dra. López", resp.getNombreVeterinario());
    }

    /**
     * Regla: si auth cae, los nombres quedan null pero la respuesta se entrega igual (degradación SUAVE).
     * Por qué este caso: verifica que una excepción en auth no mata el endpoint — el catch absorbe el error.
     *
     * GIVEN: authClient.obtenerNombre lanza RuntimeException (simula auth caído)
     *        fichaClient.obtenerNombreMascota devuelve "Firulais" (ficha sigue viva)
     * WHEN:  toResponse(cita)
     * THEN:  nombreCliente=null, nombreVeterinario=null, nombreMascota="Firulais"
     *        La respuesta se entrega sin error — degradación suave confirmada
     */
    @Test
    void testToResponseDegradaSuaveCuandoAuthCae() {
        when(authClient.obtenerNombre(any())).thenThrow(new RuntimeException("auth caído"));
        when(fichaClient.obtenerNombreMascota(any())).thenReturn("Firulais");

        Cita c = citaEnEstado("pendiente");
        c.setMascotaId(1L);

        cl.vetnova.agenda.dto.CitaResponse resp = citaService.toResponse(c);

        assertNull(resp.getNombreCliente());
        assertNull(resp.getNombreVeterinario());
        assertEquals("Firulais", resp.getNombreMascota());
    }

    /**
     * Regla: si ficha cae, nombreMascota queda null pero cliente y veterinario se entregan igual.
     *
     * GIVEN: fichaClient.obtenerNombreMascota lanza RuntimeException (ficha caída)
     *        authClient.obtenerNombre(2L) → "Juan Pérez"
     * WHEN:  toResponse(cita con mascotaId=1L)
     * THEN:  nombreMascota=null, nombreCliente="Juan Pérez" (no se pierde lo que sí llegó)
     */
    @Test
    void testToResponseDegradaSuaveCuandoFichaCae() {
        when(authClient.obtenerNombre(2L)).thenReturn("Juan Pérez");
        when(fichaClient.obtenerNombreMascota(any())).thenThrow(new RuntimeException("ficha caída"));

        Cita c = citaEnEstado("pendiente");
        c.setMascotaId(1L);

        cl.vetnova.agenda.dto.CitaResponse resp = citaService.toResponse(c);

        assertNull(resp.getNombreMascota());
        assertNotNull(resp.getNombreCliente());
    }

    /**
     * Regla: si mascotaId es null, no se llama a ficha — evita llamada remota innecesaria.
     * Por qué este caso: hay citas sin mascota asignada todavía; ficha no debe llamarse en ese caso.
     *
     * GIVEN: cita sin mascotaId (null)
     * WHEN:  toResponse(cita)
     * THEN:  nombreMascota=null, verify que fichaClient.obtenerNombreMascota NUNCA fue llamado
     */
    @Test
    void testToResponseSinMascotaIdNoLlamaaFicha() {
        when(authClient.obtenerNombre(any())).thenReturn("Juan Pérez");

        Cita c = citaEnEstado("pendiente");
        // mascotaId es null — la cita existe pero aún no tiene mascota asignada

        cl.vetnova.agenda.dto.CitaResponse resp = citaService.toResponse(c);

        assertNull(resp.getNombreMascota());
        verify(fichaClient, never()).obtenerNombreMascota(any());
    }

    /**
     * Regla: listarConNombres combina findAll con enriquecimiento de nombres.
     *
     * GIVEN: repositorio tiene 1 cita; authClient devuelve "Juan Pérez"
     * WHEN:  listarConNombres()
     * THEN:  lista de tamaño 1 con nombreCliente="Juan Pérez"
     */
    @Test
    void testListarConNombresRetornaRespuestas() {
        Cita c = citaEnEstado("pendiente");
        when(citaRepository.findAll()).thenReturn(List.of(c));
        when(authClient.obtenerNombre(any())).thenReturn("Juan Pérez");

        List<cl.vetnova.agenda.dto.CitaResponse> lista = citaService.listarConNombres();

        assertEquals(1, lista.size());
        assertEquals("Juan Pérez", lista.get(0).getNombreCliente());
    }

    /**
     * Regla: obtenerConNombres busca por id y enriquece con nombres.
     *
     * GIVEN: cita confirmada con id=1; authClient devuelve "Dr. García"
     * WHEN:  obtenerConNombres(1L)
     * THEN:  estado="confirmada", nombreCliente="Dr. García"
     */
    @Test
    void testObtenerConNombresRetornaCitaEnriquecida() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        when(authClient.obtenerNombre(any())).thenReturn("Dr. García");

        cl.vetnova.agenda.dto.CitaResponse resp = citaService.obtenerConNombres(1L);

        assertEquals("confirmada", resp.getEstado());
        assertEquals("Dr. García", resp.getNombreCliente());
    }

    /**
     * Regla: agendaDelDiaConNombres filtra por rango de día y enriquece con nombres.
     *
     * GIVEN: repositorio devuelve 1 cita en el rango del día; authClient → "Carlos Ruiz"
     * WHEN:  agendaDelDiaConNombres(LocalDateTime.now())
     * THEN:  lista de 1 elemento con nombreCliente="Carlos Ruiz"
     */
    @Test
    void testAgendaDelDiaConNombres() {
        Cita c = citaEnEstado("pendiente");
        c.setFechaHora(LocalDateTime.now().plusHours(2));
        when(citaRepository.findByFechaHoraBetweenOrderByFechaHoraAsc(any(), any()))
                .thenReturn(List.of(c));
        when(authClient.obtenerNombre(any())).thenReturn("Carlos Ruiz");

        List<cl.vetnova.agenda.dto.CitaResponse> lista =
                citaService.agendaDelDiaConNombres(LocalDateTime.now());

        assertEquals(1, lista.size());
        assertEquals("Carlos Ruiz", lista.get(0).getNombreCliente());
    }
}
