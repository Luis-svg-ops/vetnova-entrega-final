package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AsignarPermisoRequestTest {

    @Test
    void testAsignarPermisoRequestExponeSuComponente() {
        AsignarPermisoRequest request = new AsignarPermisoRequest("VER_REPORTES");
        assertEquals("VER_REPORTES", request.permiso());
    }
}
