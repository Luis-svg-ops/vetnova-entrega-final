package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import cl.vetnova.ventas.model.Carrito;

public class CarritoItemResultadoTest {

    @Test
    void testCarritoItemResultado() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        CarritoItemResultado resultado = new CarritoItemResultado(carrito, true);
        assertEquals(1L, resultado.carrito().getId());
        assertTrue(resultado.creado());
    }
}
