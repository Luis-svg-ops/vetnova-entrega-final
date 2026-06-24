package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class ProductoRequestTest {

    @Test
    void testProductoRequest() {
        ProductoRequest productoRequest = new ProductoRequest();
        productoRequest.setSku("x");
        assertEquals("x", productoRequest.getSku());
        productoRequest.setNombre("x");
        assertEquals("x", productoRequest.getNombre());
        productoRequest.setDescripcion("x");
        assertEquals("x", productoRequest.getDescripcion());
        productoRequest.setPrecio(1.0);
        assertEquals(1.0, productoRequest.getPrecio());
    }

}