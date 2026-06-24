package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SalidaStockRequestTest {

    @Test
    void testSalidaStockRequest() {
        SalidaStockRequest request = new SalidaStockRequest();
        request.setCantidad(3);
        assertEquals(3, request.getCantidad());
        request.setMotivo("Venta");
        assertEquals("Venta", request.getMotivo());
    }
}
