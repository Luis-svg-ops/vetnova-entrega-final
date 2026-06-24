package cl.vetnova.laboratorio.service;

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

import cl.vetnova.laboratorio.client.AuthClient;
import cl.vetnova.laboratorio.dto.CancelarOrdenRequest;
import cl.vetnova.laboratorio.dto.CrearOrdenExamenRequest;
import cl.vetnova.laboratorio.dto.ProgramarOrdenRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.model.TipoExamen;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;
import cl.vetnova.laboratorio.repository.TipoExamenRepository;

public class OrdenExamenServiceTest {

    @Mock private OrdenExamenRepository ordenRepository;
    @Mock private TipoExamenRepository tipoExamenRepository;
    @Mock private AuthClient authClient;
    @InjectMocks private OrdenExamenService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CrearOrdenExamenRequest crearReq(Long mascotaId, Long vetId, Long tipoId) {
        CrearOrdenExamenRequest r = new CrearOrdenExamenRequest();
        r.setMascotaId(mascotaId);
        r.setVeterinarioId(vetId);
        r.setTipoExamenId(tipoId);
        r.setDescripcion("Hemograma completo");
        return r;
    }

    private OrdenExamen orden(Long id, String estado) {
        OrdenExamen o = new OrdenExamen();
        o.setId(id);
        o.setEstado(estado);
        return o;
    }

    private ProgramarOrdenRequest progReq(LocalDateTime fecha) {
        ProgramarOrdenRequest r = new ProgramarOrdenRequest();
        r.setFechaProgramada(fecha);
        return r;
    }

    private CancelarOrdenRequest cancelarReq(String motivo) {
        CancelarOrdenRequest r = new CancelarOrdenRequest();
        r.setMotivo(motivo);
        return r;
    }

    // ---------- crear ----------
    @Test
    void testCrearMascotaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(null, 2L, 1L)));
        assertEquals("El mascotaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(1L, null, 1L)));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioSinRol() {
        when(authClient.obtenerRol(5L)).thenReturn("BODEGA");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(1L, 5L, 1L)));
        assertEquals("El usuario indicado no tiene rol de veterinario", ex.getMessage());
    }

    @Test
    void testCrearTipoExamenIdNull() {
        when(authClient.obtenerRol(2L)).thenReturn("VETERINARIO");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(1L, 2L, null)));
        assertEquals("El tipo de examen es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoExamenInexistente() {
        when(authClient.obtenerRol(2L)).thenReturn("VETERINARIO");
        when(tipoExamenRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(crearReq(1L, 2L, 999L)));
        assertEquals("Tipo de examen no encontrado", ex.getMessage());
    }

    @Test
    void testCrearFechaProgramadaAnterior() {
        when(authClient.obtenerRol(2L)).thenReturn("VETERINARIO");
        when(tipoExamenRepository.findById(1L)).thenReturn(Optional.of(new TipoExamen()));
        CrearOrdenExamenRequest r = crearReq(1L, 2L, 1L);
        r.setFechaProgramada(LocalDateTime.now().minusDays(5));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(r));
        assertEquals("La fecha programada no puede ser anterior a la fecha de solicitud", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizSinFechaProgramada() {
        when(authClient.obtenerRol(2L)).thenReturn("VETERINARIO");
        when(tipoExamenRepository.findById(1L)).thenReturn(Optional.of(new TipoExamen()));
        when(ordenRepository.save(any(OrdenExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenExamen creada = service.crear(crearReq(1L, 2L, 1L));
        assertEquals("SOLICITADA", creada.getEstado());
        assertNotNull(creada.getFechaSolicitud());
    }

    @Test
    void testCrearCasoFelizConFechaFutura() {
        when(authClient.obtenerRol(2L)).thenReturn("VETERINARIO");
        when(tipoExamenRepository.findById(1L)).thenReturn(Optional.of(new TipoExamen()));
        when(ordenRepository.save(any(OrdenExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        CrearOrdenExamenRequest r = crearReq(1L, 2L, 1L);
        r.setFechaProgramada(LocalDateTime.now().plusDays(5));
        assertNotNull(service.crear(r).getFechaProgramada());
    }

    // ---------- programar ----------
    @Test
    void testProgramarInexistente() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.programar(99L, progReq(LocalDateTime.now().plusDays(5))));
    }

    @Test
    void testProgramarFechaPasada() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "SOLICITADA")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.programar(1L, progReq(LocalDateTime.now().minusDays(5))));
        assertEquals("La fecha programada debe ser futura", ex.getMessage());
    }

    @Test
    void testProgramarFechaNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "SOLICITADA")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.programar(1L, progReq(null)));
        assertEquals("La fecha programada debe ser futura", ex.getMessage());
    }

    @Test
    void testProgramarEstadoNoSolicitada() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "EN_PROCESO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.programar(1L, progReq(LocalDateTime.now().plusDays(5))));
        assertEquals("Solo se puede programar una orden en estado SOLICITADA", ex.getMessage());
    }

    @Test
    void testProgramarCasoFeliz() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "SOLICITADA")));
        when(ordenRepository.save(any(OrdenExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenExamen prog = service.programar(1L, progReq(LocalDateTime.now().plusDays(5)));
        assertEquals("PROGRAMADA", prog.getEstado());
        assertNotNull(prog.getFechaProgramada());
    }

    // ---------- cancelar ----------
    @Test
    void testCancelarInexistente() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.cancelar(99L, cancelarReq("m")));
    }

    @Test
    void testCancelarOrdenLista() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "LISTA")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cancelar(1L, cancelarReq("m")));
        assertEquals("No se puede cancelar una orden con resultado listo", ex.getMessage());
    }

    @Test
    void testCancelarMotivoNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "PROGRAMADA")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cancelar(1L, cancelarReq(null)));
        assertEquals("El motivo de cancelación es obligatorio", ex.getMessage());
    }

    @Test
    void testCancelarMotivoVacio() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "PROGRAMADA")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cancelar(1L, cancelarReq("  ")));
        assertEquals("El motivo de cancelación es obligatorio", ex.getMessage());
    }

    @Test
    void testCancelarCasoFeliz() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "PROGRAMADA")));
        when(ordenRepository.save(any(OrdenExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenExamen cancelada = service.cancelar(1L, cancelarReq("Paciente no asistió"));
        assertEquals("CANCELADA", cancelada.getEstado());
        assertEquals("Paciente no asistió", cancelada.getMotivoCancelacion());
    }

    // ---------- listar ----------
    @Test
    void testListarSinFiltro() {
        when(ordenRepository.findAll()).thenReturn(List.of(orden(1L, "SOLICITADA")));
        assertEquals(1, service.listar(null).size());
    }

    @Test
    void testListarPorMascota() {
        when(ordenRepository.findByMascotaIdOrderByFechaSolicitudDesc(1L)).thenReturn(List.of(orden(1L, "SOLICITADA")));
        assertEquals(1, service.listar(1L).size());
    }
}
