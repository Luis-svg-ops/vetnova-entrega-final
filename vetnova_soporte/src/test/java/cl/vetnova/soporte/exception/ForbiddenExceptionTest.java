package cl.vetnova.soporte.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ForbiddenExceptionTest {

    @Test
    void testMensajeYTipoDeLaExcepcion() {
        ForbiddenException ex = new ForbiddenException("mensaje de prueba");
        assertEquals("mensaje de prueba", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
