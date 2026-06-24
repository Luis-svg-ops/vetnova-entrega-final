package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class TransferenciaStockTest {

    @Test
    void testTransferenciaStock() {
        LocalDateTime t = LocalDateTime.now();
        LocalDate d = LocalDate.of(2026, 1, 1);
        TransferenciaStock transferenciaStock = new TransferenciaStock();
        transferenciaStock.setId(1L);
        assertEquals(1L, transferenciaStock.getId());
        transferenciaStock.setProductoId(1L);
        assertEquals(1L, transferenciaStock.getProductoId());
        transferenciaStock.setSucursalOrigen("x");
        assertEquals("x", transferenciaStock.getSucursalOrigen());
        transferenciaStock.setSucursalDestino("x");
        assertEquals("x", transferenciaStock.getSucursalDestino());
        transferenciaStock.setCantidad(1);
        assertEquals(1, transferenciaStock.getCantidad());
        transferenciaStock.setEstado("x");
        assertEquals("x", transferenciaStock.getEstado());
        transferenciaStock.setFechaSolicitud(t);
        assertEquals(t, transferenciaStock.getFechaSolicitud());
        transferenciaStock.setFechaConfirmacion(t);
        assertEquals(t, transferenciaStock.getFechaConfirmacion());
    }
}
