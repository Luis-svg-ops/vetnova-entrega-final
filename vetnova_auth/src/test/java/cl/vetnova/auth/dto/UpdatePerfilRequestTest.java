package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class UpdatePerfilRequestTest {

    @Test
    void testUpdatePerfilRequestRecord() {
        UpdatePerfilRequest obj = new UpdatePerfilRequest("x", "x");
        assertEquals("x", obj.nombre());
        assertEquals("x", obj.telefono());
    }

}
