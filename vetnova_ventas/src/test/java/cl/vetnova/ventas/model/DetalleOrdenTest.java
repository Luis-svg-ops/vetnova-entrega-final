package cl.vetnova.ventas.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class DetalleOrdenTest {

    @Test
    void testDetalleOrden() {
        DetalleOrden detalleOrden = new DetalleOrden();
        detalleOrden.setId(1L);
        assertEquals(1L, detalleOrden.getId());
        detalleOrden.setOrden(new Orden());
        assertNotNull(detalleOrden.getOrden());
        detalleOrden.setProductoId(1L);
        assertEquals(1L, detalleOrden.getProductoId());
        detalleOrden.setNombreProducto("x");
        assertEquals("x", detalleOrden.getNombreProducto());
        detalleOrden.setTipoItem("PRODUCTO");
        assertEquals("PRODUCTO", detalleOrden.getTipoItem());
        detalleOrden.setCantidad(1);
        assertEquals(1, detalleOrden.getCantidad());
        detalleOrden.setPrecioUnitario(1.0);
        assertEquals(1.0, detalleOrden.getPrecioUnitario());
        detalleOrden.setSubtotal(1.0);
        assertEquals(1.0, detalleOrden.getSubtotal());
    }

}