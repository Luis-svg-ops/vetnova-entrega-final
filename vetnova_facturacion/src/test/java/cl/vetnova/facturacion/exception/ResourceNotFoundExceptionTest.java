package cl.vetnova.facturacion.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ResourceNotFoundExceptionTest {

    @Test
    void testMensajeYTipoDeLaExcepcion() {
        ResourceNotFoundException ex = new ResourceNotFoundException("mensaje de prueba");
        assertEquals("mensaje de prueba", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}