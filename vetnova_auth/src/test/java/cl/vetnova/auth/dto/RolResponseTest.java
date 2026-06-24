package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class RolResponseTest {

    @Test
    void testRolResponseRecord() {
        RolResponse obj = new RolResponse(1L, "x", "x", true, new HashSet<>());
        assertEquals(1L, obj.id());
        assertEquals("x", obj.nombreRol());
        assertEquals("x", obj.descripcion());
        assertEquals(true, obj.activo());
        assertNotNull(obj.permisos());
    }

}
