package cl.vetnova.inventario.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RegistroInmutableExceptionTest {

    @Test
    void testMensaje() {
        RegistroInmutableException ex = new RegistroInmutableException("inmutable");
        assertEquals("inmutable", ex.getMessage());
    }
}
