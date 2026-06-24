package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EscalamientoRequestTest {

    @Test
    void testGettersYSetters() {
        EscalamientoRequest r = new EscalamientoRequest();
        r.setTicketId(1L);
        r.setAdministradorId(1L);
        r.setMotivo("Sin resolución tras 72h");
        assertEquals(1L, r.getTicketId());
        assertEquals(1L, r.getAdministradorId());
        assertEquals("Sin resolución tras 72h", r.getMotivo());
    }
}
