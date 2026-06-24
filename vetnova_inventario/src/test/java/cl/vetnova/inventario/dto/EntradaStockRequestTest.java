package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EntradaStockRequestTest {

    @Test
    void testEntradaStockRequest() {
        EntradaStockRequest request = new EntradaStockRequest();
        request.setCantidad(5);
        assertEquals(5, request.getCantidad());
        request.setResponsable("Juan");
        assertEquals("Juan", request.getResponsable());
    }
}
