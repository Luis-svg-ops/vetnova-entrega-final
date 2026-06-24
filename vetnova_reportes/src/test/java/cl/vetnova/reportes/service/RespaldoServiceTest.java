package cl.vetnova.reportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Respaldo;
import cl.vetnova.reportes.repository.RespaldoRepository;

public class RespaldoServiceTest {

    @Mock private RespaldoRepository respaldoRepository;
    @InjectMocks private RespaldoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Respaldo req(String tipo, String alcance, Long ejecutadoPor, String ubicacion) {
        Respaldo r = new Respaldo();
        r.setTipo(tipo);
        r.setAlcance(alcance);
        r.setEjecutadoPor(ejecutadoPor);
        r.setUbicacion(ubicacion);
        return r;
    }

    private Respaldo respaldo(Long id, String estado) {
        Respaldo r = new Respaldo();
        r.setId(id);
        r.setEstado(estado);
        return r;
    }

    @Test
    void testEjecutarTipoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.ejecutar(req(null, "TOTAL", 1L, "/b")));
        assertEquals("El tipo es obligatorio", ex.getMessage());
    }

    @Test
    void testEjecutarTipoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.ejecutar(req("DIFERENCIAL", "TOTAL", 1L, "/b")));
        assertEquals("Tipo no válido. Valores permitidos: COMPLETO, INCREMENTAL", ex.getMessage());
    }

    @Test
    void testEjecutarAlcanceNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.ejecutar(req("COMPLETO", null, 1L, "/b")));
        assertEquals("El alcance es obligatorio", ex.getMessage());
    }

    @Test
    void testEjecutarAlcanceVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.ejecutar(req("COMPLETO", "  ", 1L, "/b")));
        assertEquals("El alcance no puede estar vacío", ex.getMessage());
    }

    @Test
    void testEjecutarEjecutadoPorNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.ejecutar(req("COMPLETO", "TOTAL", null, "/b")));
        assertEquals("El ejecutadoPor es obligatorio", ex.getMessage());
    }

    @Test
    void testEjecutarUbicacionNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.ejecutar(req("COMPLETO", "TOTAL", 1L, null)));
        assertEquals("La ubicación del respaldo es obligatoria", ex.getMessage());
    }

    @Test
    void testEjecutarTamanoNegativo() {
        Respaldo r = req("COMPLETO", "TOTAL", 1L, "/b");
        r.setTamanoBytes(-1L);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.ejecutar(r));
        assertEquals("El tamaño no puede ser negativo", ex.getMessage());
    }

    @Test
    void testEjecutarRespaldoEnCurso() {
        when(respaldoRepository.existsByEstado("EN_CURSO")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.ejecutar(req("COMPLETO", "TOTAL", 1L, "/b")));
        assertEquals("Ya hay un respaldo en curso", ex.getMessage());
    }

    @Test
    void testEjecutarCasoFeliz() {
        when(respaldoRepository.existsByEstado("EN_CURSO")).thenReturn(false);
        when(respaldoRepository.save(any(Respaldo.class))).thenAnswer(inv -> inv.getArgument(0));
        Respaldo r = req("COMPLETO", "TOTAL", 1L, "/backups/2025");
        r.setTamanoBytes(0L);
        Respaldo guardado = service.ejecutar(r);
        assertEquals("EN_CURSO", guardado.getEstado());
        assertNotNull(guardado.getFechaInicio());
    }

    @Test
    void testVerificarIntegridadFallido() {
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(respaldo(1L, "FALLIDO")));
        assertFalse(service.verificarIntegridad(1L));
    }

    @Test
    void testVerificarIntegridadCompletado() {
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(respaldo(1L, "COMPLETADO")));
        assertTrue(service.verificarIntegridad(1L));
    }

    @Test
    void testRestaurarInexistente() {
        when(respaldoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.restaurar(99L));
    }

    @Test
    void testRestaurarNoIntegro() {
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(respaldo(1L, "FALLIDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.restaurar(1L));
        assertEquals("No se puede restaurar desde un respaldo no íntegro o fallido", ex.getMessage());
    }

    @Test
    void testRestaurarCasoFeliz() {
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(respaldo(1L, "COMPLETADO")));
        assertEquals(1L, service.restaurar(1L).getId());
    }
}
