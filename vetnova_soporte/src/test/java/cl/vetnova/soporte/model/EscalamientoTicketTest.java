package cl.vetnova.soporte.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EscalamientoTicketTest {

    @Test
    void testGettersYSetters() {
        EscalamientoTicket e = new EscalamientoTicket();
        e.setId(1L);
        assertEquals(1L, e.getId());
        e.setTicketId(2L);
        assertEquals(2L, e.getTicketId());
        e.setAdministradorId(3L);
        assertEquals(3L, e.getAdministradorId());
        e.setMotivo("Sin resolución");
        assertEquals("Sin resolución", e.getMotivo());
        e.setEstado("ABIERTO");
        assertEquals("ABIERTO", e.getEstado());
        e.setUltimaAccion("Contacto telefónico");
        assertEquals("Contacto telefónico", e.getUltimaAccion());
        e.setResolucion("Reembolso aprobado");
        assertEquals("Reembolso aprobado", e.getResolucion());
        LocalDateTime ahora = LocalDateTime.now();
        e.setFechaEscalamiento(ahora);
        assertEquals(ahora, e.getFechaEscalamiento());
        e.setFechaGestion(ahora);
        assertEquals(ahora, e.getFechaGestion());
        e.setFechaResolucion(ahora);
        assertEquals(ahora, e.getFechaResolucion());
    }
}
