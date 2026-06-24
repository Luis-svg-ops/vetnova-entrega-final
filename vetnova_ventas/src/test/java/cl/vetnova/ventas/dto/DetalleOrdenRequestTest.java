package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class DetalleOrdenRequestTest {

    @Test
    void testDetalleOrdenRequest() {
        DetalleOrdenRequest detalleOrdenRequest = new DetalleOrdenRequest();
        detalleOrdenRequest.setProductoId(1L);
        assertEquals(1L, detalleOrdenRequest.getProductoId());
        detalleOrdenRequest.setNombreProducto("x");
        assertEquals("x", detalleOrdenRequest.getNombreProducto());
        detalleOrdenRequest.setCantidad(1);
        assertEquals(1, detalleOrdenRequest.getCantidad());
        detalleOrdenRequest.setPrecioUnitario(1.0);
        assertEquals(1.0, detalleOrdenRequest.getPrecioUnitario());
    }

}