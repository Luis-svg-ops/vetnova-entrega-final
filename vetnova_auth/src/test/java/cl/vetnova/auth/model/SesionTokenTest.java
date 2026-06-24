package cl.vetnova.auth.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class SesionTokenTest {

    @Test
    void testSesionToken() {
        SesionToken sesionToken = new SesionToken();
        sesionToken.setUsuario(new Usuario());
        assertNotNull(sesionToken.getUsuario());
        sesionToken.setToken("x");
        assertEquals("x", sesionToken.getToken());
        sesionToken.setExpiracion(LocalDateTime.now());
        assertNotNull(sesionToken.getExpiracion());
        sesionToken.setActivo(true);
        assertEquals(true, sesionToken.getActivo());
        assertDoesNotThrow(() -> sesionToken.getId());
        assertNotNull(new SesionToken(new Usuario(), "x", LocalDateTime.now()));
    }

    @Test
    void testSesionTokenRevocadoDejaDeSerValido() {
        SesionToken token = new SesionToken();
        token.revocar();

        assertFalse(token.esValido());
    }

    @Test
    void testSesionTokenActivoYVigenteEsValido() {
        SesionToken token = new SesionToken(new Usuario(), "token-123", LocalDateTime.now().plusMinutes(30));

        assertTrue(token.esValido());
    }

    @Test
    void testSesionTokenExpiradoNoEsValido() {
        SesionToken token = new SesionToken(new Usuario(), "token-123", LocalDateTime.now().minusMinutes(1));

        assertFalse(token.esValido());
    }

}
