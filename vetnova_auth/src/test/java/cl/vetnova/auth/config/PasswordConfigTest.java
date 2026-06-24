package cl.vetnova.auth.config;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;

public class PasswordConfigTest {

    @Test
    void testPasswordEncoderEncriptaYValida() {
        PasswordEncoder encoder = new PasswordConfig().passwordEncoder();
        String hash = encoder.encode("Clave12345");

        assertNotNull(hash);
        assertTrue(encoder.matches("Clave12345", hash));
        assertFalse(encoder.matches("otraClave", hash));
    }
}
