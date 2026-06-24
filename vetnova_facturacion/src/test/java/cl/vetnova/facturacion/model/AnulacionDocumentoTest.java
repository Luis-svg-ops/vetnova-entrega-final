package cl.vetnova.facturacion.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class AnulacionDocumentoTest {

    @Test
    void testGettersYSetters() {
        AnulacionDocumento a = new AnulacionDocumento();
        a.setId(1L);
        assertEquals(1L, a.getId());
        a.setDocumentoId(2L);
        assertEquals(2L, a.getDocumentoId());
        a.setAdministradorId(3L);
        assertEquals(3L, a.getAdministradorId());
        a.setMotivo("Monto incorrecto");
        assertEquals("Monto incorrecto", a.getMotivo());
        a.setEstadoSII("EMITIDO");
        assertEquals("EMITIDO", a.getEstadoSII());
        LocalDateTime ahora = LocalDateTime.now();
        a.setFechaAnulacion(ahora);
        assertEquals(ahora, a.getFechaAnulacion());
    }
}
