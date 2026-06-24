package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class RegistrarPagoRequestTest {

    @Test
    void testRegistrarPagoRequest() {
        RegistrarPagoRequest registrarPagoRequest = new RegistrarPagoRequest();
        registrarPagoRequest.setMetodo("x");
        assertEquals("x", registrarPagoRequest.getMetodo());
        registrarPagoRequest.setMonto(1.0);
        assertEquals(1.0, registrarPagoRequest.getMonto());
        registrarPagoRequest.setReferencia("x");
        assertEquals("x", registrarPagoRequest.getReferencia());
    }

}