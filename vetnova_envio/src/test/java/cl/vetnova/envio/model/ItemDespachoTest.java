package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ItemDespachoTest {

    @Test
    void testItemDespacho() {
        ItemDespacho item = new ItemDespacho();
        item.setId(1L);
        assertEquals(1L, item.getId());
        item.setDespachoId(2L);
        assertEquals(2L, item.getDespachoId());
        item.setProductoId(3L);
        assertEquals(3L, item.getProductoId());
        item.setCantidad(5);
        assertEquals(5, item.getCantidad());
        item.setEstado("PREPARADO");
        assertEquals("PREPARADO", item.getEstado());
    }
}
