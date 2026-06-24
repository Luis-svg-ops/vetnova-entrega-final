package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ProveedorProductoTest {

    @Test
    void testProveedorProducto() {
        ProveedorProducto pp = new ProveedorProducto();
        pp.setId(1L);
        assertEquals(1L, pp.getId());
        pp.setProveedorId(2L);
        assertEquals(2L, pp.getProveedorId());
        pp.setProductoId(3L);
        assertEquals(3L, pp.getProductoId());
    }
}
