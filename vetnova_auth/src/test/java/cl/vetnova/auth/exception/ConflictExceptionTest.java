package cl.vetnova.auth.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ConflictExceptionTest {

    @Test
    void testMensajeYTipoDeLaExcepcion() {
        ConflictException ex = new ConflictException("mensaje de prueba");
        assertEquals("mensaje de prueba", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
