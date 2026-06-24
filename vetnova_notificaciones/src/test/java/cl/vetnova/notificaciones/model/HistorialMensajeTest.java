package cl.vetnova.notificaciones.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class HistorialMensajeTest {

    @Test
    void testGettersYSetters() {
        HistorialMensaje h = new HistorialMensaje();
        h.setId(1L);
        assertEquals(1L, h.getId());
        h.setNotificacionId(2L);
        assertEquals(2L, h.getNotificacionId());
        h.setCanalId(3L);
        assertEquals(3L, h.getCanalId());
        h.setEstado("ENVIADO");
        assertEquals("ENVIADO", h.getEstado());
        h.setDescripcion("Enviado correctamente");
        assertEquals("Enviado correctamente", h.getDescripcion());
        LocalDateTime ahora = LocalDateTime.now();
        h.setFechaEnvio(ahora);
        assertEquals(ahora, h.getFechaEnvio());
    }
}
