package cl.vetnova.fichaclinica.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RegistroInmutableExceptionTest {

    @Test
    void testMensaje() {
        assertEquals("inmutable", new RegistroInmutableException("inmutable").getMessage());
    }
}
