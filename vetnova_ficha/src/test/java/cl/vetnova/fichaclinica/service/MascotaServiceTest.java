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

import cl.vetnova.fichaclinica.dto.MascotaDesactivacionResponse;
import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.model.Mascota;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.MascotaRepository;

public class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private FichaClinicaRepository fichaClinicaRepository;
    @InjectMocks
    private MascotaService mascotaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Mascota mascota(Long clienteId, String nombre, String especie) {
        Mascota m = new Mascota();
        m.setClienteId(clienteId);
        m.setNombre(nombre);
        m.setEspecie(especie);
        return m;
    }

    private Mascota valida() {
        return mascota(1L, "Rex", "PERRO");
    }

    private void guarda() {
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---- crear ----

    @Test
    void testCrearClienteIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(null, "Rex", "PERRO")));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, null, "PERRO")));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, "", "PERRO")));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearEspecieNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, "Rex", null)));
        assertEquals("La especie es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearEspecieVacia() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.crear(mascota(1L, "Rex", "")));
        assertEquals("La especie no puede estar vacía", ex.getMessage());
    }

    @Test
    void testCrearFechaNacimientoFutura() {
        Mascota m = valida();
        m.setFechaNacimiento(LocalDate.now().plusDays(1));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> mascotaService.crear(m));
        assertEquals("La fecha de nacimiento no puede ser futura", ex.getMessage());
    }

    @Test
    void testCrearPesoNoPositivo() {
        Mascota m = valida();
        m.setPeso(0.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> mascotaService.crear(m));
        assertEquals("El peso debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearMicrochipDuplicado() {
        Mascota m = valida();
        m.setMicrochip("ABC123");
        when(mascotaRepository.existsByMicrochip("ABC123")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> mascotaService.crear(m));
        assertEquals("Ya existe una mascota con ese microchip", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizCreaFicha() {
        guarda();
        Mascota m = valida();
        m.setFechaNacimiento(LocalDate.of(2020, 1, 1));
        m.setPeso(12.5);
        m.setMicrochip("XYZ999");
        Mascota creada = mascotaService.crear(m);
        assertEquals(true, creada.getActivo());
        verify(fichaClinicaRepository).save(any(FichaClinica.class));
    }

    @Test
    void testCrearSinOpcionalesEsValido() {
        guarda();
        Mascota creada = mascotaService.crear(valida());
        assertEquals(true, creada.getActivo());
    }

    // ---- actualizar (CA-MAS-15/16) ----

    @Test
    void testActualizarPesoInvalido() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        Mascota datos = valida();
        datos.setPeso(-3.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> mascotaService.actualizar(1L, datos));
        assertEquals("El peso debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testActualizarCasoFeliz() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));
        Mascota datos = valida();
        datos.setPeso(12.5);
        datos.setRaza("Golden");
        assertEquals("Golden", mascotaService.actualizar(1L, datos).getRaza());
    }

    @Test
    void testActualizarSinPesoEsValido() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(valida()));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));
        Mascota datos = valida();
        datos.setRaza("Beagle");
        assertEquals("Beagle", mascotaService.actualizar(1L, datos).getRaza());
    }

    // ---- desactivar (CA-MAS-17..19) ----

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

    @Test
    void testDesactivarYaInactivaEsIdempotente() {
        Mascota m = valida();
        m.setActivo(false);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(m));
        MascotaDesactivacionResponse resp = mascotaService.desactivar(1L);
        assertEquals("La mascota ya estaba inactiva", resp.getMensaje());
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    // ---- listar / obtener ----

    @Test
    void testListar() {
        when(mascotaRepository.findAll()).thenReturn(List.of(new Mascota()));
        assertEquals(1, mascotaService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> mascotaService.obtenerPorId(99L));
    }
}
