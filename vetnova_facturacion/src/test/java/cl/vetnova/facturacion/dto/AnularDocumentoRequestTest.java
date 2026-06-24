package cl.vetnova.facturacion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AnularDocumentoRequestTest {

    @Test
    void testGettersYSetters() {
        AnularDocumentoRequest r = new AnularDocumentoRequest();
        r.setMotivo("Error en monto");
        assertEquals("Error en monto", r.getMotivo());
    }
}
