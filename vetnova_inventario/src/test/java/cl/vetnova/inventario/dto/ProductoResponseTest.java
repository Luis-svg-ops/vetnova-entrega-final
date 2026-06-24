package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class ProductoResponseTest {

    @Test
    void testProductoResponse() {
        ProductoResponse productoResponse = new ProductoResponse();
        productoResponse.setId(1L);
        assertEquals(1L, productoResponse.getId());
        productoResponse.setSku("x");
        assertEquals("x", productoResponse.getSku());
        productoResponse.setNombre("x");
        assertEquals("x", productoResponse.getNombre());
        productoResponse.setDescripcion("x");
        assertEquals("x", productoResponse.getDescripcion());
        productoResponse.setPrecio(1.0);
        assertEquals(1.0, productoResponse.getPrecio());
        productoResponse.setActivo(true);
        assertEquals(true, productoResponse.getActivo());
        productoResponse.setFechaCreacion(LocalDateTime.now());
        assertNotNull(productoResponse.getFechaCreacion());
        productoResponse.setStock(new ArrayList<>());
        assertNotNull(productoResponse.getStock());
    }

}