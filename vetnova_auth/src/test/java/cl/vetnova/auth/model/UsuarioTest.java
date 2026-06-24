package cl.vetnova.auth.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

    @Test
    void testUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("x");
        assertEquals("x", usuario.getNombre());
        usuario.setEmail("x");
        assertEquals("x", usuario.getEmail());
        usuario.setTelefono("x");
        assertEquals("x", usuario.getTelefono());
        usuario.setPasswordHash("x");
        assertEquals("x", usuario.getPasswordHash());
        usuario.setActivo(true);
        assertEquals(true, usuario.getActivo());
        usuario.setFechaCreacion(LocalDateTime.now());
        assertNotNull(usuario.getFechaCreacion());
        usuario.setRol(new RolPermiso());
        assertNotNull(usuario.getRol());
        assertDoesNotThrow(() -> usuario.getId());
        assertNotNull(new Usuario("x", "x", "x", "x", new RolPermiso()));
    }

}
