package cl.vetnova.envio.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ConflictExceptionTest {

    @Test
    void testMensaje() {
        ConflictException ex = new ConflictException("conflicto");
        assertEquals("conflicto", ex.getMessage());
    }
}
