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

import cl.vetnova.inventario.dto.AlertaLeidaResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.AlertaStock;
import cl.vetnova.inventario.repository.AlertaStockRepository;
import cl.vetnova.inventario.repository.InventarioRepository;

public class AlertaStockServiceTest {

    @Mock
    private AlertaStockRepository alertaStockRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @InjectMocks
    private AlertaStockService alertaStockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private AlertaStock alerta(Long inventarioId, String tipo, String mensaje) {
        AlertaStock a = new AlertaStock();
        a.setInventarioId(inventarioId);
        a.setSucursal("SANTIAGO");
        a.setTipo(tipo);
        a.setMensaje(mensaje);
        a.setStockActual(2);
        a.setStockMinimo(5);
        return a;
    }

    // ---- CRUD básico ----

    @Test
    void testListar() {
        when(alertaStockRepository.findAll()).thenReturn(List.of(new AlertaStock()));
        assertEquals(1, alertaStockService.listar().size());
    }

    @Test
    void testObtenerPorIdExistente() {
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.of(new AlertaStock()));
        assertNotNull(alertaStockService.obtenerPorId(1L));
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(alertaStockRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alertaStockService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.of(new AlertaStock()));
        when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(alertaStockService.actualizar(1L, alerta(1L, "STOCK_MINIMO", "x")));
    }

    @Test
    void testActualizarInexistenteLanzaNotFound() {
        when(alertaStockRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alertaStockService.actualizar(99L, new AlertaStock()));
    }

    @Test
    void testEliminarExistente() {
        when(alertaStockRepository.existsById(1L)).thenReturn(true);
        alertaStockService.eliminar(1L);
        verify(alertaStockRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(alertaStockRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> alertaStockService.eliminar(99L));
        verify(alertaStockRepository, never()).deleteById(any());
    }

    // ---- crear (CA-ALE-01..04, 08..10) ----

    @Test
    void testCrearInventarioIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> alertaStockService.crear(alerta(null, "STOCK_MINIMO", null)));
        assertEquals("El inventarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearInventarioInexistente() {
        when(inventarioRepository.existsById(99L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> alertaStockService.crear(alerta(99L, "STOCK_MINIMO", null)));
        assertEquals("Inventario no encontrado", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> alertaStockService.crear(alerta(1L, null, null)));
        assertEquals("El tipo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> alertaStockService.crear(alerta(1L, "CRITICO", null)));
        assertEquals("Tipo no válido. Valores: STOCK_MINIMO, SIN_STOCK", ex.getMessage());
    }

    @Test
    void testCrearMensajeAutogeneradoYLeidaFalse() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        AlertaStock creada = alertaStockService.crear(alerta(1L, "STOCK_MINIMO", null));
        assertNotNull(creada.getMensaje());
        assertTrue(creada.getMensaje().contains("STOCK_MINIMO"));
        assertEquals(false, creada.getLeida());
        assertNotNull(creada.getFechaGeneracion());
    }

    @Test
    void testCrearConSinStockYMensajePropio() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        AlertaStock creada = alertaStockService.crear(alerta(1L, "SIN_STOCK", "mensaje propio"));
        assertEquals("mensaje propio", creada.getMensaje());
        assertEquals(false, creada.getLeida());
    }

    // ---- marcarLeida (CA-ALE-11..12) ----

    @Test
    void testMarcarLeidaCasoFeliz() {
        AlertaStock a = alerta(1L, "STOCK_MINIMO", "x");
        a.setLeida(false);
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.of(a));
        when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        AlertaLeidaResponse resp = alertaStockService.marcarLeida(1L);
        assertEquals(true, resp.getAlerta().getLeida());
        assertEquals("Alerta marcada como leída", resp.getMensaje());
    }

    @Test
    void testMarcarLeidaYaLeidaEsIdempotente() {
        AlertaStock a = alerta(1L, "STOCK_MINIMO", "x");
        a.setLeida(true);
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.of(a));
        AlertaLeidaResponse resp = alertaStockService.marcarLeida(1L);
        assertEquals("La alerta ya estaba leída", resp.getMensaje());
        verify(alertaStockRepository, never()).save(any(AlertaStock.class));
    }
}
