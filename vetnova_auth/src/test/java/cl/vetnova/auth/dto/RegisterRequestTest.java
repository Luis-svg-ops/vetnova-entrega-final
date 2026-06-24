package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class RegisterRequestTest {

    @Test
    void testRegisterRequestRecord() {
        RegisterRequest obj = new RegisterRequest("x", "x", "x", "x", "x");
        assertEquals("x", obj.nombre());
        assertEquals("x", obj.email());
        assertEquals("x", obj.telefono());
        assertEquals("x", obj.password());
        assertEquals("x", obj.rol());
    }

}
