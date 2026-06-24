package cl.vetnova.ventas.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ItemCarritoTest {

    @Test
    void testItemCarrito() {
        ItemCarrito item = new ItemCarrito();
        item.setId(7L);
        assertEquals(7L, item.getId());
        item.setCarritoId(3L);
        assertEquals(3L, item.getCarritoId());
        item.setItemId(1L);
        assertEquals(1L, item.getItemId());
        item.setTipo("PRODUCTO");
        assertEquals("PRODUCTO", item.getTipo());
        item.setNombre("Alimento perro");
        assertEquals("Alimento perro", item.getNombre());
        item.setCantidad(2);
        assertEquals(2, item.getCantidad());
        item.setPrecio(15000.0);
        assertEquals(15000.0, item.getPrecio());
        item.setSubtotal(30000.0);
        assertEquals(30000.0, item.getSubtotal());
    }
}
