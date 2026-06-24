package cl.vetnova.laboratorio.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RemoteServiceExceptionTest {

    @Test
    void testMensajeYTipoDeLaExcepcion() {
        RemoteServiceException ex = new RemoteServiceException("mensaje de prueba");
        assertEquals("mensaje de prueba", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}