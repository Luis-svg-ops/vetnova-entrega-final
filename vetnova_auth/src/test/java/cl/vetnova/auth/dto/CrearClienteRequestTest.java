package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CrearClienteRequestTest {

    @Test
    void testCrearClienteRequestExponeSusComponentes() {
        CrearClienteRequest r = new CrearClienteRequest(1L, "11.111.111-1", "Juan", "Pérez",
                "juan@mail.com", "+56912345678", "Calle 123");

        assertEquals(1L, r.usuarioId());
        assertEquals("11.111.111-1", r.rut());
        assertEquals("Juan", r.nombre());
        assertEquals("Pérez", r.apellido());
        assertEquals("juan@mail.com", r.email());
        assertEquals("+56912345678", r.telefono());
        assertEquals("Calle 123", r.direccion());
    }
}
