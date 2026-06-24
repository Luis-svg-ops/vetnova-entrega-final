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

import cl.vetnova.laboratorio.client.AuthClient;
import cl.vetnova.laboratorio.dto.CompletarProcesamientoRequest;
import cl.vetnova.laboratorio.dto.CrearProcesamientoRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.model.Procesamiento;
import cl.vetnova.laboratorio.repository.MuestraRepository;
import cl.vetnova.laboratorio.repository.ProcesamientoRepository;

public class ProcesamientoServiceTest {

    @Mock private ProcesamientoRepository procesamientoRepository;
    @Mock private MuestraRepository muestraRepository;
    @Mock private AuthClient authClient;
    @InjectMocks private ProcesamientoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CrearProcesamientoRequest req(Long muestraId, Long tecnicoId, String metodologia) {
        CrearProcesamientoRequest r = new CrearProcesamientoRequest();
        r.setMuestraId(muestraId);
        r.setTecnicoId(tecnicoId);
        r.setMetodologia(metodologia);
        return r;
    }

    private Muestra muestra(Long id, LocalDateTime fechaRecepcion) {
        Muestra m = new Muestra();
        m.setId(id);
        m.setFechaRecepcion(fechaRecepcion);
        return m;
    }

    private Procesamiento proc(Long id, LocalDateTime fechaInicio) {
        Procesamiento p = new Procesamiento();
        p.setId(id);
        p.setMuestraId(1L);
        p.setFechaInicio(fechaInicio);
        return p;
    }

    // ---------- crear ----------
    @Test
    void testCrearMuestraIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 3L, "Citometría")));
        assertEquals("El muestraId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMuestraInexistente() {
        when(muestraRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, 3L, "Citometría")));
        assertEquals("Muestra no encontrada", ex.getMessage());
    }

    @Test
    void testCrearMuestraNoRecibida() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 3L, "Citometría")));
        assertEquals("La muestra no ha sido recibida formalmente", ex.getMessage());
    }

    @Test
    void testCrearMuestraYaProcesamiento() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(procesamientoRepository.existsByMuestraId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, 3L, "Citometría")));
        assertEquals("La muestra ya tiene un procesamiento asociado", ex.getMessage());
    }

    @Test
    void testCrearTecnicoIdNull() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(procesamientoRepository.existsByMuestraId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, "Citometría")));
        assertEquals("El tecnicoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTecnicoInexistente() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(procesamientoRepository.existsByMuestraId(1L)).thenReturn(false);
        when(authClient.usuarioExiste(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(1L, 999L, "Citometría")));
        assertEquals("Técnico no encontrado", ex.getMessage());
    }

    @Test
    void testCrearMetodologiaNull() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(procesamientoRepository.existsByMuestraId(1L)).thenReturn(false);
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 3L, null)));
        assertEquals("La metodología es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearMetodologiaVacia() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(procesamientoRepository.existsByMuestraId(1L)).thenReturn(false);
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 3L, "  ")));
        assertEquals("La metodología no puede estar vacía", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(procesamientoRepository.existsByMuestraId(1L)).thenReturn(false);
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        when(procesamientoRepository.save(any(Procesamiento.class))).thenAnswer(inv -> inv.getArgument(0));
        Procesamiento p = service.crear(req(1L, 3L, "Citometría de flujo"));
        assertEquals("EN_PROCESO", p.getEstado());
        assertNull(p.getFechaInicio());
    }

    // ---------- iniciar ----------
    @Test
    void testIniciarInexistente() {
        when(procesamientoRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.iniciar(99L));
        assertEquals("Procesamiento no encontrado", ex.getMessage());
    }

    @Test
    void testIniciarYaIniciado() {
        when(procesamientoRepository.findById(1L)).thenReturn(Optional.of(proc(1L, LocalDateTime.now())));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.iniciar(1L));
        assertEquals("El procesamiento ya fue iniciado", ex.getMessage());
    }

    @Test
    void testIniciarCasoFeliz() {
        when(procesamientoRepository.findById(1L)).thenReturn(Optional.of(proc(1L, null)));
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(muestraRepository.save(any(Muestra.class))).thenAnswer(inv -> inv.getArgument(0));
        when(procesamientoRepository.save(any(Procesamiento.class))).thenAnswer(inv -> inv.getArgument(0));
        Procesamiento p = service.iniciar(1L);
        assertNotNull(p.getFechaInicio());
    }

    // ---------- completar ----------
    @Test
    void testCompletarSinIniciar() {
        when(procesamientoRepository.findById(1L)).thenReturn(Optional.of(proc(1L, null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.completar(1L, completar(null, null)));
        assertEquals("No se puede completar un procesamiento que no fue iniciado", ex.getMessage());
    }

    @Test
    void testCompletarFechaFinAnterior() {
        when(procesamientoRepository.findById(1L)).thenReturn(Optional.of(proc(1L, LocalDateTime.of(2025, 6, 1, 8, 0))));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.completar(1L, completar(LocalDateTime.of(2025, 5, 1, 8, 0), null)));
        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio", ex.getMessage());
    }

    @Test
    void testCompletarCasoFelizSinFechaFin() {
        when(procesamientoRepository.findById(1L)).thenReturn(Optional.of(proc(1L, LocalDateTime.now().minusHours(2))));
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(muestraRepository.save(any(Muestra.class))).thenAnswer(inv -> inv.getArgument(0));
        when(procesamientoRepository.save(any(Procesamiento.class))).thenAnswer(inv -> inv.getArgument(0));
        Procesamiento p = service.completar(1L, completar(null, null));
        assertEquals("COMPLETADO", p.getEstado());
        assertNotNull(p.getFechaFin());
    }

    @Test
    void testCompletarCasoFelizConFechaFin() {
        LocalDateTime inicio = LocalDateTime.now().minusHours(2);
        when(procesamientoRepository.findById(1L)).thenReturn(Optional.of(proc(1L, inicio)));
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, LocalDateTime.now())));
        when(muestraRepository.save(any(Muestra.class))).thenAnswer(inv -> inv.getArgument(0));
        when(procesamientoRepository.save(any(Procesamiento.class))).thenAnswer(inv -> inv.getArgument(0));
        LocalDateTime fin = inicio.plusHours(1);
        Procesamiento p = service.completar(1L, completar(fin, "Listo"));
        assertEquals(fin, p.getFechaFin());
        assertEquals("Listo", p.getObservaciones());
    }

    private CompletarProcesamientoRequest completar(LocalDateTime fechaFin, String observaciones) {
        CompletarProcesamientoRequest r = new CompletarProcesamientoRequest();
        r.setFechaFin(fechaFin);
        r.setObservaciones(observaciones);
        return r;
    }
}
