package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class UsuarioResponseTest {

    @Test
    void testUsuarioResponseRecord() {
        UsuarioResponse obj = new UsuarioResponse(1L, "x", "x", "x", "x", new HashSet<>(), true, LocalDateTime.now());
        assertEquals(1L, obj.id());
        assertEquals("x", obj.nombre());
        assertEquals("x", obj.email());
        assertEquals("x", obj.telefono());
        assertEquals("x", obj.rol());
        assertNotNull(obj.permisos());
        assertEquals(true, obj.activo());
        assertNotNull(obj.fechaCreacion());
    }

}
