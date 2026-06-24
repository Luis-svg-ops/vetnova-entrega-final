package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DerivarTicketRequestTest {

    @Test
    void testGettersYSetters() {
        DerivarTicketRequest r = new DerivarTicketRequest();
        r.setResponsableId(3L);
        assertEquals(3L, r.getResponsableId());
    }
}
