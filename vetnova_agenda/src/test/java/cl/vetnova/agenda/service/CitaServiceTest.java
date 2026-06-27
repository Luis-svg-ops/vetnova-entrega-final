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

    private static final LocalDateTime FUTURO = LocalDateTime.of(2030, 7, 1, 10, 0);

    @Mock private CitaRepository citaRepository;
    @Mock private RecordatorioGenerador recordatorioGenerador;
    @Mock private AuthClient authClient;
    @Mock private FichaClient fichaClient;
    @InjectMocks private CitaService citaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Por defecto, el cliente existe y la mascota también (degradación controlada en tests de crear)
        doNothing().when(authClient).verificarCliente(any());
        doNothing().when(fichaClient).verificarMascota(any());
    }

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

    private Cita citaConfirmada(LocalDateTime fecha, Integer duracion) {
        Cita c = new Cita();
        c.setVeterinarioId(4L);
        c.setEstado("confirmada");
        c.setFechaHora(fecha);
        c.setDuracionMinutos(duracion);
        return c;
    }

    private Cita citaEnEstado(String estado) {
        Cita c = citaValida();
        c.setId(1L);
        c.setEstado(estado);
        return c;
    }

    private void guarda() {
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---- crear ----

    @Test
    void testCrearClienteNull() {
        Cita c = citaValida();
        c.setClienteId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioNull() {
        Cita c = citaValida();
        c.setVeterinarioId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearServicioNull() {
        Cita c = citaValida();
        c.setServicioId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("El servicioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFechaNull() {
        Cita c = citaValida();
        c.setFechaHora(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("La fecha y hora son obligatorias", ex.getMessage());
    }

    @Test
    void testCrearFechaEnPasado() {
        Cita c = citaValida();
        c.setFechaHora(LocalDateTime.of(2020, 1, 1, 10, 0));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("La fecha y hora deben ser futuras", ex.getMessage());
    }

    @Test
    void testCrearSucursalNull() {
        Cita c = citaValida();
        c.setSucursal(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.crear(c));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearSucursalInexistente() {
        Cita c = citaValida();
        c.setSucursal("FANTASMA");
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> citaService.crear(c));
        assertEquals("Sucursal no encontrada", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioNoDisponiblePorSolapamiento() {
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO, null)));
        Cita nueva = citaValida();
        nueva.setFechaHora(FUTURO.plusMinutes(15));
        ConflictException ex = assertThrows(ConflictException.class, () -> citaService.crear(nueva));
        assertEquals("El veterinario no está disponible en ese horario", ex.getMessage());
    }

    @Test
    void testCrearSinSolapamientoExistenteAnterior() {
        guarda();
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO.minusHours(2), 30)));
        assertEquals("pendiente", citaService.crear(citaValida()).getEstado());
    }

    @Test
    void testCrearSinSolapamientoExistentePosterior() {
        guarda();
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO.plusHours(2), 30)));
        assertEquals("pendiente", citaService.crear(citaValida()).getEstado());
    }

    @Test
    void testCrearCasoFelizSinCitasPrevias() {
        guarda();
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada")).thenReturn(List.of());
        Cita c = citaValida();
        c.setDuracionMinutos(null);
        Cita creada = citaService.crear(c);
        assertEquals("pendiente", creada.getEstado());
        assertNotNull(creada.getFechaCreacion());
        verify(recordatorioGenerador).generarParaCita(creada);
    }

    // ---- confirmar ----

    @Test
    void testConfirmarCanceladaEsInvalido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("cancelada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.confirmar(1L));
        assertEquals("No se puede confirmar cita cancelada", ex.getMessage());
    }

    @Test
    void testConfirmarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        assertEquals("confirmada", citaService.confirmar(1L).getEstado());
    }

    @Test
    void testConfirmarCitaInexistenteLanzaNotFound() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> citaService.confirmar(99L));
    }

    // ---- iniciar ----

    @Test
    void testIniciarDesdePendienteEsInvalido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.iniciar(1L));
        assertEquals("Debe estar confirmada antes de iniciarse", ex.getMessage());
    }

    @Test
    void testIniciarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        assertEquals("en curso", citaService.iniciar(1L).getEstado());
    }

    // ---- completar ----

    @Test
    void testCompletarDesdePendienteEsInvalido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.completar(1L));
        assertEquals("Debe estar en curso antes de completarse", ex.getMessage());
    }

    @Test
    void testCompletarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("en curso")));
        assertEquals("completada", citaService.completar(1L).getEstado());
    }

    // ---- cancelar ----

    @Test
    void testCancelarCompletadaEsImpedido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("completada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.cancelar(1L, "x"));
        assertEquals("No se puede cancelar cita completada", ex.getMessage());
    }

    @Test
    void testCancelarMotivoNull() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> citaService.cancelar(1L, null));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testCancelarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        Cita cancelada = citaService.cancelar(1L, "Emergencia");
        assertEquals("cancelada", cancelada.getEstado());
        assertEquals("Emergencia", cancelada.getMotivoCancelacion());
        verify(recordatorioGenerador).cancelarPorCita(cancelada.getId());
    }

    // ---- listar / obtener ----

    @Test
    void testListar() {
        when(citaRepository.findAll()).thenReturn(List.of(new Cita()));
        assertEquals(1, citaService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> citaService.obtenerPorId(99L));
    }

    // ---- reprogramar ----

    @Test
    void testReprogramarCasoFeliz() {
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        Cita result = citaService.reprogramar(1L, FUTURO.plusDays(1), 45);
        assertEquals(FUTURO.plusDays(1), result.getFechaHora());
        assertEquals(45, result.getDuracionMinutos());
    }

    @Test
    void testReprogramarFechaNullLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, null, null));
        assertEquals("La nueva fecha y hora son obligatorias", ex.getMessage());
    }

    @Test
    void testReprogramarFechaPasadaLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, LocalDateTime.of(2020, 1, 1, 10, 0), null));
        assertEquals("La nueva fecha y hora deben ser futuras", ex.getMessage());
    }

    @Test
    void testReprogramarCitaCompletadaLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("completada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, FUTURO.plusDays(1), null));
        assertTrue(ex.getMessage().contains("completada"));
    }

    @Test
    void testReprogramarCitaCanceladaLanzaException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("cancelada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> citaService.reprogramar(1L, FUTURO.plusDays(1), null));
        assertTrue(ex.getMessage().contains("cancelada"));
    }

    @Test
    void testReprogramarConSolapamientoLanzaConflict() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        when(citaRepository.findByVeterinarioIdAndEstado(4L, "confirmada"))
                .thenReturn(List.of(citaConfirmada(FUTURO.plusDays(1), 60)));
        assertThrows(ConflictException.class,
                () -> citaService.reprogramar(1L, FUTURO.plusDays(1), 30));
    }

    @Test
    void testReprogramarSinDuracionConservaDuracionOriginal() {
        Cita original = citaEnEstado("pendiente");
        original.setDuracionMinutos(50);
        guarda();
        when(citaRepository.findById(1L)).thenReturn(Optional.of(original));
        Cita result = citaService.reprogramar(1L, FUTURO.plusDays(2), null);
        assertEquals(50, result.getDuracionMinutos());
    }

    // ---- toResponse / listarConNombres / obtenerConNombres ----

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

    @Test
    void testToResponseSinMascotaIdNoLlamaaFicha() {
        when(authClient.obtenerNombre(any())).thenReturn("Juan Pérez");

        Cita c = citaEnEstado("pendiente");
        // mascotaId es null

        cl.vetnova.agenda.dto.CitaResponse resp = citaService.toResponse(c);

        assertNull(resp.getNombreMascota());
        verify(fichaClient, never()).obtenerNombreMascota(any());
    }

    @Test
    void testListarConNombresRetornaRespuestas() {
        Cita c = citaEnEstado("pendiente");
        when(citaRepository.findAll()).thenReturn(List.of(c));
        when(authClient.obtenerNombre(any())).thenReturn("Juan Pérez");

        List<cl.vetnova.agenda.dto.CitaResponse> lista = citaService.listarConNombres();

        assertEquals(1, lista.size());
        assertEquals("Juan Pérez", lista.get(0).getNombreCliente());
    }

    @Test
    void testObtenerConNombresRetornaCitaEnriquecida() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEnEstado("confirmada")));
        when(authClient.obtenerNombre(any())).thenReturn("Dr. García");

        cl.vetnova.agenda.dto.CitaResponse resp = citaService.obtenerConNombres(1L);

        assertEquals("confirmada", resp.getEstado());
        assertEquals("Dr. García", resp.getNombreCliente());
    }

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
