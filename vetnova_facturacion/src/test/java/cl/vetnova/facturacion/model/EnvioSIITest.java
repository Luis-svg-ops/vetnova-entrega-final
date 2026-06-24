package cl.vetnova.facturacion.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EnvioSIITest {

    @Test
    void testGettersYSetters() {
        EnvioSII e = new EnvioSII();
        e.setId(1L);
        assertEquals(1L, e.getId());
        e.setDocumentoId(2L);
        assertEquals(2L, e.getDocumentoId());
        e.setRespuestaCodigo("ACEPTADO");
        assertEquals("ACEPTADO", e.getRespuestaCodigo());
        e.setRespuestaDescripcion("Documento aceptado");
        assertEquals("Documento aceptado", e.getRespuestaDescripcion());
        e.setEstado("ENVIADO");
        assertEquals("ENVIADO", e.getEstado());
        e.setReintentado(false);
        assertFalse(e.getReintentado());
        LocalDateTime ahora = LocalDateTime.now();
        e.setFechaEnvio(ahora);
        assertEquals(ahora, e.getFechaEnvio());
    }
}
