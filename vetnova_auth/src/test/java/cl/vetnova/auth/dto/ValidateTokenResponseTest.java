package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class ValidateTokenResponseTest {

    @Test
    void testValidateTokenResponseRecord() {
        ValidateTokenResponse obj = new ValidateTokenResponse(true, 1L, "x", new HashSet<>());
        assertEquals(true, obj.valido());
        assertEquals(1L, obj.usuarioId());
        assertEquals("x", obj.rol());
        assertNotNull(obj.permisos());
    }

}
