package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class CrearOrdenExamenRequestTest {

    @Test
    void testGettersYSetters() {
        CrearOrdenExamenRequest r = new CrearOrdenExamenRequest();
        r.setMascotaId(1L);
        r.setVeterinarioId(2L);
        r.setTipoExamenId(3L);
        r.setDescripcion("Hemograma completo");
        LocalDateTime f = LocalDateTime.now();
        r.setFechaProgramada(f);
        assertEquals(1L, r.getMascotaId());
        assertEquals(2L, r.getVeterinarioId());
        assertEquals(3L, r.getTipoExamenId());
        assertEquals("Hemograma completo", r.getDescripcion());
        assertEquals(f, r.getFechaProgramada());
    }
}
