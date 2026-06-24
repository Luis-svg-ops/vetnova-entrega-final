package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DerivacionRequestTest {

    @Test
    void testGettersYSetters() {
        DerivacionRequest r = new DerivacionRequest();
        r.setTicketId(1L);
        r.setResponsableNuevo(3L);
        r.setMotivo("Especialista requerido");
        assertEquals(1L, r.getTicketId());
        assertEquals(3L, r.getResponsableNuevo());
        assertEquals("Especialista requerido", r.getMotivo());
    }
}
