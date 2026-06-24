package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CrearPagoRequestTest {

    @Test
    void testCrearPagoRequest() {
        CrearPagoRequest request = new CrearPagoRequest();
        request.setOrdenId(1L);
        assertEquals(1L, request.getOrdenId());
        request.setMetodo("TARJETA");
        assertEquals("TARJETA", request.getMetodo());
        request.setMonto(1500.0);
        assertEquals(1500.0, request.getMonto());
        request.setReferencia("TXN-1");
        assertEquals("TXN-1", request.getReferencia());
    }
}
