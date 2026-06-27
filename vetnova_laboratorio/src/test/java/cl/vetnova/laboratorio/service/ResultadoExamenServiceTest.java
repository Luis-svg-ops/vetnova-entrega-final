package cl.vetnova.laboratorio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.laboratorio.client.AuthClient;
import cl.vetnova.laboratorio.dto.RegistrarResultadoRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.model.ResultadoExamen;
import cl.vetnova.laboratorio.repository.MuestraRepository;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;
import cl.vetnova.laboratorio.repository.ResultadoExamenRepository;

public class ResultadoExamenServiceTest {

    @Mock private ResultadoExamenRepository resultadoRepository;
    @Mock private OrdenExamenRepository ordenRepository;
    @Mock private MuestraRepository muestraRepository;
    @Mock private AuthClient authClient;
    @InjectMocks private ResultadoExamenService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private RegistrarResultadoRequest req(Long ordenId, Long muestraId, Long tecnicoId, String resultado) {
        RegistrarResultadoRequest r = new RegistrarResultadoRequest();
        r.setOrdenExamenId(ordenId);
        r.setMuestraId(muestraId);
        r.setTecnicoId(tecnicoId);
        r.setResultado(resultado);
        return r;
    }

    private OrdenExamen orden(Long id, String estado) {
        OrdenExamen o = new OrdenExamen();
        o.setId(id);
        o.setEstado(estado);
        return o;
    }

    private Muestra muestra(Long id, String estado) {
        Muestra m = new Muestra();
        m.setId(id);
        m.setEstadoProcesamiento(estado);
        return m;
    }

    private ResultadoExamen resultado(Long id, Boolean disponible) {
        ResultadoExamen r = new ResultadoExamen();
        r.setId(id);
        r.setOrdenExamenId(1L);
        r.setDisponible(disponible);
        return r;
    }

    private void ordenEnProceso() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "EN_PROCESO")));
        when(resultadoRepository.existsByOrdenExamenId(1L)).thenReturn(false);
    }

    // ---------- registrar ----------
    @Test
    void testRegistrarOrdenIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(null, 1L, 3L, "Normal")));
        assertEquals("El ordenExamenId es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarOrdenInexistente() {
        when(ordenRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrar(req(999L, 1L, 3L, "Normal")));
        assertEquals("Orden de examen no encontrada", ex.getMessage());
    }

    @Test
    void testRegistrarOrdenNoProcesada() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "PROGRAMADA")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 1L, 3L, "Normal")));
        assertEquals("No se puede registrar resultado de una orden que no está en proceso", ex.getMessage());
    }

    @Test
    void testRegistrarOrdenYaTieneResultado() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "EN_PROCESO")));
        when(resultadoRepository.existsByOrdenExamenId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.registrar(req(1L, 1L, 3L, "Normal")));
        assertEquals("La orden ya tiene un resultado registrado", ex.getMessage());
    }

    @Test
    void testRegistrarMuestraNoProcesada() {
        ordenEnProceso();
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "EN_PROCESO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 1L, 3L, "Normal")));
        assertEquals("La muestra aún no ha sido procesada", ex.getMessage());
    }

    @Test
    void testRegistrarSinMuestraIdTecnicoNull() {
        ordenEnProceso();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, null, null, "Normal")));
        assertEquals("El muestraId es obligatorio para registrar un resultado", ex.getMessage());
    }

    @Test
    void testRegistrarMuestraInexistenteTecnicoInexistente() {
        ordenEnProceso();
        when(muestraRepository.findById(1L)).thenReturn(Optional.empty());
        when(authClient.usuarioExiste(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrar(req(1L, 1L, 999L, "Normal")));
        assertEquals("Muestra no encontrada", ex.getMessage());
    }

    @Test
    void testRegistrarResultadoNull() {
        ordenEnProceso();
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "PROCESADA")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 1L, 3L, null)));
        assertEquals("El resultado es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarResultadoVacio() {
        ordenEnProceso();
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "PROCESADA")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 1L, 3L, "  ")));
        assertEquals("El resultado no puede estar vacío", ex.getMessage());
    }

    @Test
    void testRegistrarCasoFeliz() {
        ordenEnProceso();
        when(muestraRepository.findById(1L)).thenReturn(Optional.of(muestra(1L, "PROCESADA")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        when(resultadoRepository.save(any(ResultadoExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        ResultadoExamen r = service.registrar(req(1L, 1L, 3L, "Hemograma normal"));
        assertFalse(r.getDisponible());
        assertNotNull(r.getFechaRegistro());
    }

    // ---------- publicar ----------
    @Test
    void testPublicarInexistente() {
        when(resultadoRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.publicar(99L));
        assertEquals("Resultado no encontrado", ex.getMessage());
    }

    @Test
    void testPublicarYaPublicado() {
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultado(1L, true)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.publicar(1L));
        assertEquals("El resultado ya fue publicado", ex.getMessage());
    }

    @Test
    void testPublicarCasoFeliz() {
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultado(1L, false)));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, "EN_PROCESO")));
        when(ordenRepository.save(any(OrdenExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        when(resultadoRepository.save(any(ResultadoExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        ResultadoExamen r = service.publicar(1L);
        assertTrue(r.getDisponible());
    }
}
