package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DetallePedidoRequestTest {

    @Test
    void testDetallePedidoRequest() {
        DetallePedidoRequest request = new DetallePedidoRequest();
        request.setPedidoId(1L);
        assertEquals(1L, request.getPedidoId());
        request.setProductoId(2L);
        assertEquals(2L, request.getProductoId());
        request.setCantidad(10);
        assertEquals(10, request.getCantidad());
        request.setPrecioUnitario(800.0);
        assertEquals(800.0, request.getPrecioUnitario());
    }
}
