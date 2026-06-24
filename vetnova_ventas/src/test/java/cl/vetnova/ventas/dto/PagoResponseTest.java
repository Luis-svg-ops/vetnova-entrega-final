package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class PagoResponseTest {

    @Test
    void testPagoResponse() {
        PagoResponse pagoResponse = new PagoResponse();
        pagoResponse.setId(1L);
        assertEquals(1L, pagoResponse.getId());
        pagoResponse.setOrdenId(1L);
        assertEquals(1L, pagoResponse.getOrdenId());
        pagoResponse.setMetodo("x");
        assertEquals("x", pagoResponse.getMetodo());
        pagoResponse.setMonto(1.0);
        assertEquals(1.0, pagoResponse.getMonto());
        pagoResponse.setEstado("x");
        assertEquals("x", pagoResponse.getEstado());
        pagoResponse.setReferencia("x");
        assertEquals("x", pagoResponse.getReferencia());
        pagoResponse.setFecha(LocalDateTime.now());
        assertNotNull(pagoResponse.getFecha());
    }

}