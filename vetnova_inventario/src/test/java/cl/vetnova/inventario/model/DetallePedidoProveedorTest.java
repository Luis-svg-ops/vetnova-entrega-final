package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DetallePedidoProveedorTest {

    @Test
    void testDetallePedidoProveedor() {
        DetallePedidoProveedor detalle = new DetallePedidoProveedor();
        detalle.setId(1L);
        assertEquals(1L, detalle.getId());
        detalle.setPedidoId(2L);
        assertEquals(2L, detalle.getPedidoId());
        detalle.setProductoId(3L);
        assertEquals(3L, detalle.getProductoId());
        detalle.setCantidad(10);
        assertEquals(10, detalle.getCantidad());
        detalle.setPrecioUnitario(500.0);
        assertEquals(500.0, detalle.getPrecioUnitario());
        detalle.setSubtotal(5000.0);
        assertEquals(5000.0, detalle.getSubtotal());
    }
}
