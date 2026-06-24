package cl.vetnova.soporte.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class RespuestaTicketTest {

    @Test
    void testGettersYSetters() {
        RespuestaTicket r = new RespuestaTicket();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setTicketId(2L);
        assertEquals(2L, r.getTicketId());
        r.setAutorId(3L);
        assertEquals(3L, r.getAutorId());
        r.setContenido("Hemos revisado su caso");
        assertEquals("Hemos revisado su caso", r.getContenido());
        r.setVisible(true);
        assertTrue(r.getVisible());
        LocalDateTime ahora = LocalDateTime.now();
        r.setFecha(ahora);
        assertEquals(ahora, r.getFecha());
    }
}
