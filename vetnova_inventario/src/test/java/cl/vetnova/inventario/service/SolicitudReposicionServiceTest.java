package cl.vetnova.inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.inventario.dto.ResolucionRequest;
import cl.vetnova.inventario.dto.SolicitudReposicionRequest;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.SolicitudReposicion;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.SolicitudReposicionRepository;

public class SolicitudReposicionServiceTest {

    @Mock
    private SolicitudReposicionRepository solicitudReposicionRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @InjectMocks
    private SolicitudReposicionService solicitudReposicionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private SolicitudReposicionRequest request(Long inventarioId, Integer cantidad, String motivo, Long solicitadoPor) {
        SolicitudReposicionRequest r = new SolicitudReposicionRequest();
        r.setInventarioId(inventarioId);
        r.setCantidadSolicitada(cantidad);
        r.setMotivo(motivo);
        r.setSolicitadoPor(solicitadoPor);
        return r;
    }

    private ResolucionRequest resolucion(Long aprobadoPor, String motivo) {
        ResolucionRequest r = new ResolucionRequest();
        r.setAprobadoPor(aprobadoPor);
        r.setMotivo(motivo);
        return r;
    }

    private SolicitudReposicion solicitud(String estado) {
        SolicitudReposicion s = new SolicitudReposicion();
        s.setId(1L);
        s.setInventarioId(1L);
        s.setEstado(estado);
        return s;
    }

    private void inventarioExiste() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    void testListar() {
        when(solicitudReposicionRepository.findAll()).thenReturn(List.of(new SolicitudReposicion()));
        assertEquals(1, solicitudReposicionService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(solicitudReposicionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> solicitudReposicionService.obtenerPorId(99L));
    }

    // ---- crear (CA-SOL-01..08, 11, 12) ----

    @Test
    void testCrearInventarioIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.crear(request(null, 5, "falta", 2L)));
        assertEquals("El inventarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearInventarioInexistente() {
        when(inventarioRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> solicitudReposicionService.crear(request(999L, 5, "falta", 2L)));
        assertEquals("Inventario no encontrado", ex.getMessage());
    }

    @Test
    void testCrearCantidadNull() {
        inventarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.crear(request(1L, null, "falta", 2L)));
        assertEquals("La cantidad solicitada es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearCantidadNoPositiva() {
        inventarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.crear(request(1L, 0, "falta", 2L)));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearMotivoNull() {
        inventarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.crear(request(1L, 5, null, 2L)));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMotivoVacio() {
        inventarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.crear(request(1L, 5, "   ", 2L)));
        assertEquals("El motivo no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearSolicitanteNull() {
        inventarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.crear(request(1L, 5, "falta", null)));
        assertEquals("El solicitante es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearDuplicadaPendiente() {
        inventarioExiste();
        when(solicitudReposicionRepository.existsByInventarioIdAndEstado(1L, "pendiente")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> solicitudReposicionService.crear(request(1L, 5, "falta", 2L)));
        assertEquals("Ya existe solicitud pendiente para este inventario", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        inventarioExiste();
        when(solicitudReposicionRepository.existsByInventarioIdAndEstado(1L, "pendiente")).thenReturn(false);
        when(solicitudReposicionRepository.save(any(SolicitudReposicion.class))).thenAnswer(inv -> inv.getArgument(0));
        SolicitudReposicion creada = solicitudReposicionService.crear(request(1L, 5, "falta", 2L));
        assertEquals("pendiente", creada.getEstado());
        assertNull(creada.getAprobadoPor());
        assertNotNull(creada.getFechaSolicitud());
    }

    // ---- aprobar (CA-SOL-13, 15, 16) ----

    @Test
    void testAprobarYaResuelta() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("aprobada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.aprobar(1L, resolucion(2L, null)));
        assertEquals("La solicitud ya fue resuelta", ex.getMessage());
    }

    @Test
    void testAprobarAprobadorNull() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.aprobar(1L, resolucion(null, null)));
        assertEquals("El aprobador es obligatorio", ex.getMessage());
    }

    @Test
    void testAprobarCasoFeliz() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("pendiente")));
        when(solicitudReposicionRepository.save(any(SolicitudReposicion.class))).thenAnswer(inv -> inv.getArgument(0));
        SolicitudReposicion aprobada = solicitudReposicionService.aprobar(1L, resolucion(2L, null));
        assertEquals("aprobada", aprobada.getEstado());
        assertEquals(2L, aprobada.getAprobadoPor());
        assertNotNull(aprobada.getFechaResolucion());
    }

    // ---- rechazar (CA-SOL-17, 18) ----

    @Test
    void testRechazarYaResuelta() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("rechazada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.rechazar(1L, resolucion(2L, "x")));
        assertEquals("La solicitud ya fue resuelta", ex.getMessage());
    }

    @Test
    void testRechazarCasoFeliz() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("pendiente")));
        when(solicitudReposicionRepository.save(any(SolicitudReposicion.class))).thenAnswer(inv -> inv.getArgument(0));
        SolicitudReposicion rechazada = solicitudReposicionService.rechazar(1L, resolucion(2L, "Presupuesto"));
        assertEquals("rechazada", rechazada.getEstado());
        assertEquals("Presupuesto", rechazada.getMotivoRechazo());
        assertNotNull(rechazada.getFechaResolucion());
    }

    // ---- actualizar (CA-SOL-19) ----

    @Test
    void testActualizarAprobadaInmutable() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("aprobada")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.actualizar(1L, solicitud("aprobada")));
        assertEquals("No se puede modificar solicitud resuelta", ex.getMessage());
    }

    @Test
    void testActualizarRechazadaInmutable() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("rechazada")));
        assertThrows(BusinessRuleException.class,
                () -> solicitudReposicionService.actualizar(1L, solicitud("rechazada")));
    }

    @Test
    void testActualizarPendienteCasoFeliz() {
        when(solicitudReposicionRepository.findById(1L)).thenReturn(Optional.of(solicitud("pendiente")));
        when(solicitudReposicionRepository.save(any(SolicitudReposicion.class))).thenAnswer(inv -> inv.getArgument(0));
        SolicitudReposicion datos = new SolicitudReposicion();
        datos.setCantidadSolicitada(99);
        assertEquals(99, solicitudReposicionService.actualizar(1L, datos).getCantidadSolicitada());
    }
}
