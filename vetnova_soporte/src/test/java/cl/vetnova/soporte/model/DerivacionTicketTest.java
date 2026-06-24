package cl.vetnova.soporte.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DerivacionTicketTest {

    @Test
    void testGettersYSetters() {
        DerivacionTicket d = new DerivacionTicket();
        d.setId(1L);
        assertEquals(1L, d.getId());
        d.setTicketId(2L);
        assertEquals(2L, d.getTicketId());
        d.setResponsableAnterior(3L);
        assertEquals(3L, d.getResponsableAnterior());
        d.setResponsableNuevo(4L);
        assertEquals(4L, d.getResponsableNuevo());
        d.setMotivo("Reasignación");
        assertEquals("Reasignación", d.getMotivo());
        LocalDateTime ahora = LocalDateTime.now();
        d.setFecha(ahora);
        assertEquals(ahora, d.getFecha());
    }
}
