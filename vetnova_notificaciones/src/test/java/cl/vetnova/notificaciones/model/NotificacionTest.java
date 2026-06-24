package cl.vetnova.notificaciones.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class NotificacionTest {

    @Test
    void testGettersYSetters() {
        Notificacion n = new Notificacion();
        n.setId(1L);
        assertEquals(1L, n.getId());
        n.setUsuarioId(2L);
        assertEquals(2L, n.getUsuarioId());
        n.setTipo("EMAIL");
        assertEquals("EMAIL", n.getTipo());
        n.setMensaje("Cita confirmada");
        assertEquals("Cita confirmada", n.getMensaje());
        n.setLeida(false);
        assertFalse(n.getLeida());
        n.setEstado("ENVIADO");
        assertEquals("ENVIADO", n.getEstado());
        LocalDateTime ahora = LocalDateTime.now();
        n.setFechaEnvio(ahora);
        assertEquals(ahora, n.getFechaEnvio());
        n.setFechaLectura(ahora);
        assertEquals(ahora, n.getFechaLectura());
    }
}
