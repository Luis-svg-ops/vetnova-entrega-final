package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class RolRequestTest {

    @Test
    void testRolRequestRecord() {
        RolRequest obj = new RolRequest("x", "x", new HashSet<>());
        assertEquals("x", obj.nombreRol());
        assertEquals("x", obj.descripcion());
        assertNotNull(obj.permisos());
    }

}
