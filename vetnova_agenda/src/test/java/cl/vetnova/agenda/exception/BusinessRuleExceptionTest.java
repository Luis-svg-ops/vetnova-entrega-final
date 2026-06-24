package cl.vetnova.agenda.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BusinessRuleExceptionTest {

    @Test
    void testMensaje() {
        BusinessRuleException ex = new BusinessRuleException("regla");
        assertEquals("regla", ex.getMessage());
    }
}
