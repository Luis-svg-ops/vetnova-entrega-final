package cl.vetnova.agenda.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class BloqueAgendaTest {

    @Test
    void testBloqueAgenda() {
        LocalDateTime inicio = LocalDateTime.of(2030, 7, 1, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2030, 7, 1, 17, 0);
        BloqueAgenda bloque = new BloqueAgenda();
        bloque.setId(1L);
        assertEquals(1L, bloque.getId());
        bloque.setVeterinarioId(4L);
        assertEquals(4L, bloque.getVeterinarioId());
        bloque.setBoxId(2L);
        assertEquals(2L, bloque.getBoxId());
        bloque.setFechaInicio(inicio);
        assertEquals(inicio, bloque.getFechaInicio());
        bloque.setFechaFin(fin);
        assertEquals(fin, bloque.getFechaFin());
        bloque.setMotivo("Vacaciones");
        assertEquals("Vacaciones", bloque.getMotivo());
        bloque.setCreadoPor(5L);
        assertEquals(5L, bloque.getCreadoPor());
        bloque.setTipo("BLOQUEO");
        assertEquals("BLOQUEO", bloque.getTipo());
    }
}
