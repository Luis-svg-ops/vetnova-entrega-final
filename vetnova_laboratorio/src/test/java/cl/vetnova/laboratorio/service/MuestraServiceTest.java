package cl.vetnova.laboratorio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.laboratorio.dto.ActualizarEstadoMuestraRequest;
import cl.vetnova.laboratorio.dto.RecepcionMuestraRequest;
import cl.vetnova.laboratorio.dto.RegistrarMuestraRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.model.TipoExamen;
import cl.vetnova.laboratorio.repository.MuestraRepository;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;

public class MuestraServiceTest {

    @Mock private MuestraRepository muestraRepository;
    @Mock private OrdenExamenRepository ordenRepository;
    @InjectMocks private MuestraService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private RegistrarMuestraRequest req(Long ordenId, String tipo, String codigo) {
        RegistrarMuestraRequest r = new RegistrarMuestraRequest();
        r.setOrdenExamenId(ordenId);
        r.setTipo(tipo);
        r.setCodigoMuestra(codigo);
        return r;
    }

    private OrdenExamen orden(Long id, boolean requiereMuestra) {
        OrdenExamen o = new OrdenExamen();
        o.setId(id);
        o.setEstado("PROGRAMADA");
        TipoExamen t = new TipoExamen();
        t.setRequiereMuestra(requiereMuestra);
        o.setTipoExamen(t);
        return o;
    }

    private Muestra muestra(Long id, String estado, LocalDateTime fechaRecepcion) {
        Muestra m = new Muestra();
        m.setId(id);
        m.setOrdenExamenId(1L);
        m.setEstadoProcesamiento(estado);
        m.setFechaRecepcion(fechaRecepcion);
        return m;
    }

    // ---------- crear ----------
    @Test
    void testCrearOrdenIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, "SANGRE", "M-001")));
        assertEquals("El ordenExamenId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearOrdenInexistente() {
        when(ordenRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, "SANGRE", "M-001")));
        assertEquals("Orden de examen no encontrada", ex.getMessage());
    }

    @Test
    void testCrearOrdenNoRequiereMuestra() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "SANGRE", "M-001")));
        assertEquals("Este tipo de examen no requiere muestra", ex.getMessage());
    }

    @Test
    void testCrearOrdenYaTieneMuestra() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, true)));
        when(muestraRepository.existsByOrdenExamenId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, "SANGRE", "M-001")));
        assertEquals("La orden ya tiene una muestra registrada", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, true)));
        when(muestraRepository.existsByOrdenExamenId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, "M-001")));
        assertEquals("El tipo de muestra es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoVacio() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, true)));
        when(muestraRepository.existsByOrdenExamenId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "  ", "M-001")));
        assertEquals("El tipo de muestra no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearCodigoNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, true)));
        when(muestraRepository.existsByOrdenExamenId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "SANGRE", null)));
        assertEquals("El código de muestra es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearCodigoDuplicado() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, true)));
        when(muestraRepository.existsByOrdenExamenId(1L)).thenReturn(false);
        when(muestraRepository.existsByCodigoMuestra("M-001")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, "SANGRE", "M-001")));
        assertEquals("Ya existe una muestra con ese código", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, true)));
        when(muestraRepository.existsByOrdenExamenId(1L)).thenReturn(false);
        when(muestraRepository.existsByCodigoMuestra("M-001")).thenReturn(false);
        when(muestraRepository.save(any(Muestra.class))).thenAnswer(inv -> inv.getArgument(0));
        Muestra creada = service.crear(req(1L, "SANGRE", "M-001"));
        assertEquals("RECIBIDA", creada.getEstadoProcesamiento());
    }

    // ---------- registrarRecepcion ----------
    @Test
    void testRecepcionMuestraInexistente() {
        when(muestraRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrarRecepcion(99L, recepcion(3L)));
        assertEquals("Muestra no encontrada", ex.getMessage());
    }

    @Test
    void testRecepcionResponsableNull() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "RECIBIDA", null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrarRecepcion(1L, recepcion(null)));
        assertEquals("El responsable de recepción es obligatorio", ex.getMessage());
    }

    @Test
    void testRecepcionYaRecibida() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "RECIBIDA", LocalDateTime.now())));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrarRecepcion(1L, recepcion(3L)));
        assertEquals("La muestra ya fue recibida", ex.getMessage());
    }

    @Test
    void testRecepcionCasoFeliz() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "RECIBIDA", null)));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, true)));
        when(ordenRepository.save(any(OrdenExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        when(muestraRepository.save(any(Muestra.class))).thenAnswer(inv -> inv.getArgument(0));
        Muestra m = service.registrarRecepcion(1L, recepcion(3L));
        assertNotNull(m.getFechaRecepcion());
        assertEquals(3L, m.getResponsableRecepcion());
    }

    // ---------- actualizarEstado ----------
    @Test
    void testActualizarEstadoInvalido() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "RECIBIDA", null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.actualizarEstado(1L, estado("PERDIDA")));
        assertEquals("Estado no válido. Valores permitidos: RECIBIDA, EN_PROCESO, PROCESADA, DESCARTADA", ex.getMessage());
    }

    @Test
    void testActualizarEstadoNull() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "RECIBIDA", null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.actualizarEstado(1L, estado(null)));
        assertEquals("Estado no válido. Valores permitidos: RECIBIDA, EN_PROCESO, PROCESADA, DESCARTADA", ex.getMessage());
    }

    @Test
    void testActualizarEstadoTransicionInvalida() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "PROCESADA", null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.actualizarEstado(1L, estado("RECIBIDA")));
        assertEquals("Transición de estado no permitida: PROCESADA → RECIBIDA", ex.getMessage());
    }

    @Test
    void testActualizarEstadoCasoFeliz() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "RECIBIDA", null)));
        when(muestraRepository.save(any(Muestra.class))).thenAnswer(inv -> inv.getArgument(0));
        Muestra m = service.actualizarEstado(1L, estado("EN_PROCESO"));
        assertEquals("EN_PROCESO", m.getEstadoProcesamiento());
    }

    private RecepcionMuestraRequest recepcion(Long responsableId) {
        RecepcionMuestraRequest r = new RecepcionMuestraRequest();
        r.setResponsableId(responsableId);
        return r;
    }

    private ActualizarEstadoMuestraRequest estado(String estado) {
        ActualizarEstadoMuestraRequest r = new ActualizarEstadoMuestraRequest();
        r.setEstado(estado);
        return r;
    }
}
