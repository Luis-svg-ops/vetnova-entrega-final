package cl.vetnova.facturacion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EnvioSIIRequestTest {

    @Test
    void testGettersYSetters() {
        EnvioSIIRequest r = new EnvioSIIRequest();
        r.setDocumentoId(1L);
        assertEquals(1L, r.getDocumentoId());
    }
}
