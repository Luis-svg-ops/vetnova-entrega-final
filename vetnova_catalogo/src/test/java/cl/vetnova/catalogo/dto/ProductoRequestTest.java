package cl.vetnova.catalogo.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class ProductoRequestTest {

    @Test
    void testProductoRequest() {
        ProductoRequest productoRequest = new ProductoRequest();
        productoRequest.setNombre("x");
        assertEquals("x", productoRequest.getNombre());
        productoRequest.setDescripcion("x");
        assertEquals("x", productoRequest.getDescripcion());
        productoRequest.setPrecio(1.0);
        assertEquals(1.0, productoRequest.getPrecio());
        productoRequest.setCategoriaId(1L);
        assertEquals(1L, productoRequest.getCategoriaId());
        productoRequest.setImagenUrl("x");
        assertEquals("x", productoRequest.getImagenUrl());
    }

}