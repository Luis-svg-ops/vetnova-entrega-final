package cl.vetnova.notificaciones.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BusinessRuleExceptionTest {

    @Test
    void testMensajeYTipoDeLaExcepcion() {
        BusinessRuleException ex = new BusinessRuleException("mensaje de prueba");
        assertEquals("mensaje de prueba", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
