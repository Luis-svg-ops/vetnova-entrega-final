package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class LoginRequestTest {

    @Test
    void testLoginRequestRecord() {
        LoginRequest obj = new LoginRequest("x", "x");
        assertEquals("x", obj.email());
        assertEquals("x", obj.password());
    }

}
