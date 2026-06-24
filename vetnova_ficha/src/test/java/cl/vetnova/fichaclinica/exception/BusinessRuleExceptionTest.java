package cl.vetnova.fichaclinica.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BusinessRuleExceptionTest {

    @Test
    void testMensaje() {
        assertEquals("regla", new BusinessRuleException("regla").getMessage());
    }
}
