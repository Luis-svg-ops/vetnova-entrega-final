package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ResolucionRequestTest {

    @Test
    void testResolucionRequest() {
        ResolucionRequest request = new ResolucionRequest();
        request.setAprobadoPor(2L);
        assertEquals(2L, request.getAprobadoPor());
        request.setMotivo("Presupuesto");
        assertEquals("Presupuesto", request.getMotivo());
    }
}
