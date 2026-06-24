package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import cl.vetnova.inventario.model.TransferenciaStock;

public class CancelacionResponseTest {

    @Test
    void testConstructorYSetters() {
        TransferenciaStock t = new TransferenciaStock();
        t.setId(1L);
        CancelacionResponse resp = new CancelacionResponse(t, "ok");
        assertEquals(1L, resp.getTransferencia().getId());
        assertEquals("ok", resp.getMensaje());

        CancelacionResponse vacio = new CancelacionResponse();
        vacio.setTransferencia(t);
        vacio.setMensaje("otro");
        assertEquals(t, vacio.getTransferencia());
        assertEquals("otro", vacio.getMensaje());
    }
}
