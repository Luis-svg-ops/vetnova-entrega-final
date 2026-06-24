package cl.vetnova.soporte.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ValoracionTest {

    @Test
    void testGettersYSetters() {
        Valoracion v = new Valoracion();
        v.setId(1L);
        assertEquals(1L, v.getId());
        v.setTicketId(2L);
        assertEquals(2L, v.getTicketId());
        v.setClienteId(3L);
        assertEquals(3L, v.getClienteId());
        v.setPuntuacion(5);
        assertEquals(5, v.getPuntuacion());
        v.setComentario("Excelente atención");
        assertEquals("Excelente atención", v.getComentario());
        v.setSucursalId(4L);
        assertEquals(4L, v.getSucursalId());
        LocalDateTime ahora = LocalDateTime.now();
        v.setFecha(ahora);
        assertEquals(ahora, v.getFecha());
    }
}
