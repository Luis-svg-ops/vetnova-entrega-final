package cl.vetnova.fichaclinica.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ConflictExceptionTest {

    @Test
    void testMensaje() {
        assertEquals("conflicto", new ConflictException("conflicto").getMessage());
    }
}
