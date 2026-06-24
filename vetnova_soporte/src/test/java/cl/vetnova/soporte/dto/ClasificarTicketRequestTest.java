package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ClasificarTicketRequestTest {

    @Test
    void testGettersYSetters() {
        ClasificarTicketRequest r = new ClasificarTicketRequest();
        r.setCategoriaId(1L);
        r.setPrioridad("ALTA");
        assertEquals(1L, r.getCategoriaId());
        assertEquals("ALTA", r.getPrioridad());
    }
}
