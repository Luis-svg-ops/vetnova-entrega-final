package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RechazarPagoRequestTest {

    @Test
    void testRechazarPagoRequest() {
        RechazarPagoRequest request = new RechazarPagoRequest();
        request.setMotivo("fondos insuficientes");
        assertEquals("fondos insuficientes", request.getMotivo());
    }
}
