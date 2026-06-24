package cl.vetnova.auth.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class AuditoriaAccesoTest {

    @Test
    void testAuditoriaAcceso() {
        AuditoriaAcceso auditoriaAcceso = new AuditoriaAcceso();
        auditoriaAcceso.setUsuario(new Usuario());
        assertNotNull(auditoriaAcceso.getUsuario());
        auditoriaAcceso.setAccion("x");
        assertEquals("x", auditoriaAcceso.getAccion());
        auditoriaAcceso.setIp("x");
        assertEquals("x", auditoriaAcceso.getIp());
        auditoriaAcceso.setTimestamp(LocalDateTime.now());
        assertNotNull(auditoriaAcceso.getTimestamp());
        auditoriaAcceso.setExitoso(true);
        assertEquals(true, auditoriaAcceso.getExitoso());
        auditoriaAcceso.setDetalle("x");
        assertEquals("x", auditoriaAcceso.getDetalle());
        assertDoesNotThrow(() -> auditoriaAcceso.getId());
        assertNotNull(new AuditoriaAcceso(new Usuario(), "x", "x", true, "x"));
    }

}
