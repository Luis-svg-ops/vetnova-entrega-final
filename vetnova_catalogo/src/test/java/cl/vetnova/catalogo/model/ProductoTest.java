package cl.vetnova.catalogo.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ProductoTest {

    @Test
    void testProducto() {
        Producto producto = new Producto();
        producto.setId(1L);
        assertEquals(1L, producto.getId());
        producto.setNombre("x");
        assertEquals("x", producto.getNombre());
        producto.setDescripcion("x");
        assertEquals("x", producto.getDescripcion());
        producto.setPrecio(1.0);
        assertEquals(1.0, producto.getPrecio());
        producto.setActivo(true);
        assertEquals(true, producto.getActivo());
        producto.setCategoriaId(1L);
        assertEquals(1L, producto.getCategoriaId());
        producto.setImagenUrl("x");
        assertEquals("x", producto.getImagenUrl());
        producto.setFechaActualizacion(LocalDate.now());
        assertNotNull(producto.getFechaActualizacion());
    }

}