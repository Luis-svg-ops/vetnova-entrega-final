package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CerrarTicketRequestTest {

    @Test
    void testGettersYSetters() {
        CerrarTicketRequest r = new CerrarTicketRequest();
        r.setResolucion("Reembolso procesado");
        assertEquals("Reembolso procesado", r.getResolucion());
    }
}
