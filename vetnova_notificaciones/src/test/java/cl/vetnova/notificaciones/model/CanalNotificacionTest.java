package cl.vetnova.notificaciones.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CanalNotificacionTest {

    @Test
    void testGettersYSetters() {
        CanalNotificacion c = new CanalNotificacion();
        c.setId(1L);
        assertEquals(1L, c.getId());
        c.setUsuarioId(2L);
        assertEquals(2L, c.getUsuarioId());
        c.setTipo("EMAIL");
        assertEquals("EMAIL", c.getTipo());
        c.setDestino("juan@mail.com");
        assertEquals("juan@mail.com", c.getDestino());
        c.setActivo(true);
        assertTrue(c.getActivo());
    }
}
