package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ActualizarClienteRequestTest {

    @Test
    void testActualizarClienteRequestExponeSusComponentes() {
        ActualizarClienteRequest r = new ActualizarClienteRequest("+56987654321", "Nueva dirección 456");

        assertEquals("+56987654321", r.telefono());
        assertEquals("Nueva dirección 456", r.direccion());
    }
}
