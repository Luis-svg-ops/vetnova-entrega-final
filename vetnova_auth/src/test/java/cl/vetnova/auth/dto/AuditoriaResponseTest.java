package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import cl.vetnova.auth.model.AuditoriaAcceso;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.model.Usuario;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class AuditoriaResponseTest {

    @Test
    void testFromConUsuario() {
        Usuario usuario = new Usuario("Juan", "juan@mail.com", "+569", "hash",
                new RolPermiso("CLIENTE", "d", Set.of("VER_PERFIL")));
        AuditoriaAcceso a = new AuditoriaAcceso(usuario, "LOGIN", "192.168.1.1", true, "ok");

        AuditoriaResponse r = AuditoriaResponse.from(a);

        assertEquals("LOGIN", r.accion());
        assertEquals("192.168.1.1", r.ip());
        assertTrue(r.exitoso());
        assertNotNull(r.fechaHora());
    }

    @Test
    void testFromSinUsuarioDejaUsuarioIdNull() {
        AuditoriaAcceso a = new AuditoriaAcceso(null, "LOGIN", "192.168.1.1", false, null);

        AuditoriaResponse r = AuditoriaResponse.from(a);

        assertNull(r.usuarioId());
        assertFalse(r.exitoso());
    }
}
