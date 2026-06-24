package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class MovimientoStockResponseTest {

    @Test
    void testMovimientoStockResponse() {
        MovimientoStockResponse movimientoStockResponse = new MovimientoStockResponse();
        movimientoStockResponse.setId(1L);
        assertEquals(1L, movimientoStockResponse.getId());
        movimientoStockResponse.setInventarioId(2L);
        assertEquals(2L, movimientoStockResponse.getInventarioId());
        movimientoStockResponse.setTipo("SALIDA");
        assertEquals("SALIDA", movimientoStockResponse.getTipo());
        movimientoStockResponse.setCantidad(3);
        assertEquals(3, movimientoStockResponse.getCantidad());
        movimientoStockResponse.setMotivo("Venta");
        assertEquals("Venta", movimientoStockResponse.getMotivo());
        movimientoStockResponse.setResponsable("Ana");
        assertEquals("Ana", movimientoStockResponse.getResponsable());
        movimientoStockResponse.setSucursal("SANTIAGO");
        assertEquals("SANTIAGO", movimientoStockResponse.getSucursal());
        LocalDateTime t = LocalDateTime.now();
        movimientoStockResponse.setFecha(t);
        assertEquals(t, movimientoStockResponse.getFecha());
        movimientoStockResponse.setStockResultante(7);
        assertEquals(7, movimientoStockResponse.getStockResultante());
    }
}
