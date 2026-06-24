package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CrearUsuarioRequestTest {

    @Test
    void testCrearUsuarioRequestExponeSusComponentes() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("Juan", "juan@mail.com", "+56912345678", "Pass1234!", "CLIENTE");

        assertEquals("Juan", request.nombre());
        assertEquals("juan@mail.com", request.email());
        assertEquals("+56912345678", request.telefono());
        assertEquals("Pass1234!", request.password());
        assertEquals("CLIENTE", request.nombreRol());
    }
}
