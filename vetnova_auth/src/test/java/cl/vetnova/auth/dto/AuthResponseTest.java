package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class AuthResponseTest {

    @Test
    void testAuthResponseRecord() {
        UsuarioResponse usuario = new UsuarioResponse(1L, "x", "x", "x", "x", new HashSet<>(), true, LocalDateTime.now());
        AuthResponse obj = new AuthResponse("token-123", LocalDateTime.now(), usuario);
        assertEquals("token-123", obj.token());
        assertNotNull(obj.expiracion());
        assertNotNull(obj.usuario());
    }

}
