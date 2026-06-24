package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class ErrorResponseTest {

    @Test
    void testErrorResponseRecord() {
        ErrorResponse obj = new ErrorResponse(true, "x", "x", 1, new HashMap<>(), LocalDateTime.now());
        assertEquals(true, obj.success());
        assertEquals("x", obj.message());
        assertEquals("x", obj.path());
        assertEquals(1, obj.status());
        assertNotNull(obj.errors());
        assertNotNull(obj.timestamp());
    }

}