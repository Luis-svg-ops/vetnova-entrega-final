package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class PasswordChangeRequestTest {

    @Test
    void testPasswordChangeRequestRecord() {
        PasswordChangeRequest obj = new PasswordChangeRequest("x", "x");
        assertEquals("x", obj.actual());
        assertEquals("x", obj.nueva());
    }

}
