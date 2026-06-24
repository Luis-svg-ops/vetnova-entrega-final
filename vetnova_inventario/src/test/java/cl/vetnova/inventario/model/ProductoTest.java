package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class ProductoTest {

    @Test
    void testProducto() {
        Producto producto = new Producto();
        producto.setId(1L);
        assertEquals(1L, producto.getId());
        producto.setSku("x");
        assertEquals("x", producto.getSku());
        producto.setNombre("x");
        assertEquals("x", producto.getNombre());
        producto.setDescripcion("x");
        assertEquals("x", producto.getDescripcion());
        producto.setPrecio(1.0);
        assertEquals(1.0, producto.getPrecio());
        producto.setActivo(true);
        assertEquals(true, producto.getActivo());
        producto.setFechaCreacion(LocalDateTime.now());
        assertNotNull(producto.getFechaCreacion());
        producto.setStockSucursales(new ArrayList<>());
        assertNotNull(producto.getStockSucursales());
    }

}