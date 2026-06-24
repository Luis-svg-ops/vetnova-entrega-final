package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CrearAuditoriaRequestTest {

    @Test
    void testCrearAuditoriaRequestExponeSusComponentes() {
        CrearAuditoriaRequest r = new CrearAuditoriaRequest(1L, "LOGIN", "192.168.1.1", true, "ok");

        assertEquals(1L, r.usuarioId());
        assertEquals("LOGIN", r.accion());
        assertEquals("192.168.1.1", r.ip());
        assertTrue(r.exitoso());
        assertEquals("ok", r.detalles());
    }
}
