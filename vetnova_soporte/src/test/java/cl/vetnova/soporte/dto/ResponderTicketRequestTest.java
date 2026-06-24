package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ResponderTicketRequestTest {

    @Test
    void testGettersYSetters() {
        ResponderTicketRequest r = new ResponderTicketRequest();
        r.setAutorId(3L);
        r.setContenido("Hemos revisado su caso");
        r.setVisible(true);
        assertEquals(3L, r.getAutorId());
        assertEquals("Hemos revisado su caso", r.getContenido());
        assertTrue(r.getVisible());
    }
}
