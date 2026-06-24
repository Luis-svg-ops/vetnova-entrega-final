package cl.vetnova.catalogo.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class ProductoResponseTest {

    @Test
    void testProductoResponse() {
        ProductoResponse productoResponse = new ProductoResponse();
        productoResponse.setId(1L);
        assertEquals(1L, productoResponse.getId());
        productoResponse.setNombre("x");
        assertEquals("x", productoResponse.getNombre());
        productoResponse.setDescripcion("x");
        assertEquals("x", productoResponse.getDescripcion());
        productoResponse.setPrecio(1.0);
        assertEquals(1.0, productoResponse.getPrecio());
        productoResponse.setActivo(true);
        assertEquals(true, productoResponse.getActivo());
        productoResponse.setCategoriaId(1L);
        assertEquals(1L, productoResponse.getCategoriaId());
        productoResponse.setImagenUrl("x");
        assertEquals("x", productoResponse.getImagenUrl());
        productoResponse.setFechaActualizacion(LocalDate.now());
        assertNotNull(productoResponse.getFechaActualizacion());
    }

}