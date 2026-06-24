package cl.vetnova.auth.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UnauthorizedExceptionTest {

    @Test
    void testMensajeYTipoDeLaExcepcion() {
        UnauthorizedException ex = new UnauthorizedException("credenciales inválidas");
        assertEquals("credenciales inválidas", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
