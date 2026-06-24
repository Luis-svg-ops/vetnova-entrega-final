package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MovimientoStockRequestTest {

    @Test
    void testMovimientoStockRequest() {
        MovimientoStockRequest movimientoStockRequest = new MovimientoStockRequest();
        movimientoStockRequest.setInventarioId(1L);
        assertEquals(1L, movimientoStockRequest.getInventarioId());
        movimientoStockRequest.setTipo("ENTRADA");
        assertEquals("ENTRADA", movimientoStockRequest.getTipo());
        movimientoStockRequest.setCantidad(5);
        assertEquals(5, movimientoStockRequest.getCantidad());
        movimientoStockRequest.setMotivo("Compra");
        assertEquals("Compra", movimientoStockRequest.getMotivo());
        movimientoStockRequest.setResponsable("Juan");
        assertEquals("Juan", movimientoStockRequest.getResponsable());
    }
}
