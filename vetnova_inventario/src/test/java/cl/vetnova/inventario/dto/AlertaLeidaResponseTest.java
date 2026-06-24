package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import cl.vetnova.inventario.model.AlertaStock;

public class AlertaLeidaResponseTest {

    @Test
    void testConstructorYSetters() {
        AlertaStock a = new AlertaStock();
        a.setId(1L);
        AlertaLeidaResponse resp = new AlertaLeidaResponse(a, "ok");
        assertEquals(1L, resp.getAlerta().getId());
        assertEquals("ok", resp.getMensaje());

        AlertaLeidaResponse vacio = new AlertaLeidaResponse();
        vacio.setAlerta(a);
        vacio.setMensaje("otro");
        assertEquals(a, vacio.getAlerta());
        assertEquals("otro", vacio.getMensaje());
    }
}
