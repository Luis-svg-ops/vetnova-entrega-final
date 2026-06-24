package cl.vetnova.facturacion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AnulacionRequestTest {

    @Test
    void testGettersYSetters() {
        AnulacionRequest r = new AnulacionRequest();
        r.setDocumentoId(1L);
        r.setAdministradorId(2L);
        r.setMotivo("Monto incorrecto");
        assertEquals(1L, r.getDocumentoId());
        assertEquals(2L, r.getAdministradorId());
        assertEquals("Monto incorrecto", r.getMotivo());
    }
}
