package cl.vetnova.soporte.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TicketTest {

    @Test
    void testGettersYSetters() {
        Ticket t = new Ticket();
        t.setId(1L);
        assertEquals(1L, t.getId());
        t.setClienteId(2L);
        assertEquals(2L, t.getClienteId());
        t.setMotivo("Producto defectuoso");
        assertEquals("Producto defectuoso", t.getMotivo());
        t.setDescripcion("Detalle");
        assertEquals("Detalle", t.getDescripcion());
        CategoriaTicket c = new CategoriaTicket();
        t.setCategoria(c);
        assertEquals(c, t.getCategoria());
        t.setPrioridad("ALTA");
        assertEquals("ALTA", t.getPrioridad());
        t.setEstado("ABIERTO");
        assertEquals("ABIERTO", t.getEstado());
        t.setResponsableId(3L);
        assertEquals(3L, t.getResponsableId());
        t.setSucursalId(4L);
        assertEquals(4L, t.getSucursalId());
        LocalDateTime ahora = LocalDateTime.now();
        t.setFechaCreacion(ahora);
        assertEquals(ahora, t.getFechaCreacion());
        t.setFechaCierre(ahora);
        assertEquals(ahora, t.getFechaCierre());
        t.setResolucion("Resuelto");
        assertEquals("Resuelto", t.getResolucion());
    }
}
