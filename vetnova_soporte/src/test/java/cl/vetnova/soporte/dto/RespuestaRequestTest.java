package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RespuestaRequestTest {

    @Test
    void testGettersYSetters() {
        RespuestaRequest r = new RespuestaRequest();
        r.setTicketId(1L);
        r.setAutorId(3L);
        r.setContenido("Hemos procesado su solicitud");
        r.setVisible(false);
        assertEquals(1L, r.getTicketId());
        assertEquals(3L, r.getAutorId());
        assertEquals("Hemos procesado su solicitud", r.getContenido());
        assertFalse(r.getVisible());
    }
}
