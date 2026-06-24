package cl.vetnova.ventas.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class PagoTest {

    @Test
    void testPago() {
        Pago pago = new Pago();
        pago.setId(1L);
        assertEquals(1L, pago.getId());
        pago.setOrden(new Orden());
        assertNotNull(pago.getOrden());
        pago.setMetodo("x");
        assertEquals("x", pago.getMetodo());
        pago.setMonto(1.0);
        assertEquals(1.0, pago.getMonto());
        pago.setEstado("x");
        assertEquals("x", pago.getEstado());
        pago.setReferencia("x");
        assertEquals("x", pago.getReferencia());
        pago.setMotivoRechazo("fondos");
        assertEquals("fondos", pago.getMotivoRechazo());
        pago.setFecha(LocalDateTime.now());
        assertNotNull(pago.getFecha());
    }

}