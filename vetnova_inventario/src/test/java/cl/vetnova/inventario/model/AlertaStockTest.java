package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class AlertaStockTest {

    @Test
    void testAlertaStock() {
        LocalDateTime t = LocalDateTime.now();
        AlertaStock alertaStock = new AlertaStock();
        alertaStock.setId(1L);
        assertEquals(1L, alertaStock.getId());
        alertaStock.setInventarioId(1L);
        assertEquals(1L, alertaStock.getInventarioId());
        alertaStock.setSucursal("x");
        assertEquals("x", alertaStock.getSucursal());
        alertaStock.setTipo("STOCK_MINIMO");
        assertEquals("STOCK_MINIMO", alertaStock.getTipo());
        alertaStock.setMensaje("stock bajo");
        assertEquals("stock bajo", alertaStock.getMensaje());
        alertaStock.setStockActual(1);
        assertEquals(1, alertaStock.getStockActual());
        alertaStock.setStockMinimo(1);
        assertEquals(1, alertaStock.getStockMinimo());
        alertaStock.setLeida(true);
        assertEquals(true, alertaStock.getLeida());
        alertaStock.setFechaGeneracion(t);
        assertEquals(t, alertaStock.getFechaGeneracion());
    }
}
