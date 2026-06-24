package cl.vetnova.facturacion.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RegistroInmutableExceptionTest {

    @Test
    void testMensajeYTipoDeLaExcepcion() {
        RegistroInmutableException ex = new RegistroInmutableException("mensaje de prueba");
        assertEquals("mensaje de prueba", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
