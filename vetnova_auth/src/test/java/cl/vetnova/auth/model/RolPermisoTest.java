package cl.vetnova.auth.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class RolPermisoTest {

    @Test
    void testRolPermiso() {
        RolPermiso rolPermiso = new RolPermiso();
        rolPermiso.setNombreRol("x");
        assertEquals("x", rolPermiso.getNombreRol());
        rolPermiso.setDescripcion("x");
        assertEquals("x", rolPermiso.getDescripcion());
        rolPermiso.setActivo(true);
        assertEquals(true, rolPermiso.getActivo());
        rolPermiso.setPermisos(new HashSet<>());
        assertNotNull(rolPermiso.getPermisos());
        assertDoesNotThrow(() -> rolPermiso.getId());
        assertNotNull(new RolPermiso("x", "x", new HashSet<>()));
    }

    @Test
    void testRolPermisoAsignaVerificaYRevocaPermisos() {
        RolPermiso rol = new RolPermiso("CLIENTE", "Rol cliente", new HashSet<>(java.util.Set.of("AGENDAR_CITA")));

        assertTrue(rol.tienePermiso("AGENDAR_CITA"));
        rol.asignarPermiso("VER_CATALOGO");
        assertTrue(rol.tienePermiso("VER_CATALOGO"));
        rol.revocarPermiso("AGENDAR_CITA");
        assertFalse(rol.tienePermiso("AGENDAR_CITA"));
    }

    @Test
    void testRolPermisoConPermisosNulosCreaUnSetVacio() {
        RolPermiso rol = new RolPermiso("INVITADO", "Rol sin permisos", null);

        assertNotNull(rol.getPermisos());
        assertTrue(rol.getPermisos().isEmpty());
        assertTrue(rol.getActivo());
    }

}
