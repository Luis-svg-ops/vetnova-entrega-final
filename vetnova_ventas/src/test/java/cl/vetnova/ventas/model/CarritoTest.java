package cl.vetnova.ventas.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CarritoTest {

    @Test
    void testCarrito() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        assertEquals(1L, carrito.getId());
        carrito.setClienteId(2L);
        assertEquals(2L, carrito.getClienteId());
        carrito.setTotal(45000.0);
        assertEquals(45000.0, carrito.getTotal());
        carrito.setActivo(true);
        assertEquals(true, carrito.getActivo());
        carrito.setItems(List.of(new ItemCarrito()));
        assertEquals(1, carrito.getItems().size());
    }
}
