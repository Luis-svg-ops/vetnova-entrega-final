package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class DetalleOrdenResponseTest {

    @Test
    void testDetalleOrdenResponse() {
        DetalleOrdenResponse detalleOrdenResponse = new DetalleOrdenResponse();
        detalleOrdenResponse.setId(1L);
        assertEquals(1L, detalleOrdenResponse.getId());
        detalleOrdenResponse.setProductoId(1L);
        assertEquals(1L, detalleOrdenResponse.getProductoId());
        detalleOrdenResponse.setNombreProducto("x");
        assertEquals("x", detalleOrdenResponse.getNombreProducto());
        detalleOrdenResponse.setCantidad(1);
        assertEquals(1, detalleOrdenResponse.getCantidad());
        detalleOrdenResponse.setPrecioUnitario(1.0);
        assertEquals(1.0, detalleOrdenResponse.getPrecioUnitario());
        detalleOrdenResponse.setSubtotal(1.0);
        assertEquals(1.0, detalleOrdenResponse.getSubtotal());
    }

}