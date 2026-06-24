package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GestionarEscalamientoRequestTest {

    @Test
    void testGettersYSetters() {
        GestionarEscalamientoRequest r = new GestionarEscalamientoRequest();
        r.setAccion("Contacto telefónico realizado");
        assertEquals("Contacto telefónico realizado", r.getAccion());
    }
}
