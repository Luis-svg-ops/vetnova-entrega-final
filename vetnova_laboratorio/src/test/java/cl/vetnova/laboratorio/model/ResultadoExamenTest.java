package cl.vetnova.laboratorio.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ResultadoExamenTest {

    @Test
    void testGettersYSetters() {
        ResultadoExamen r = new ResultadoExamen();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setOrdenExamenId(2L);
        assertEquals(2L, r.getOrdenExamenId());
        r.setMuestraId(3L);
        assertEquals(3L, r.getMuestraId());
        r.setTecnicoId(4L);
        assertEquals(4L, r.getTecnicoId());
        r.setResultado("Hemograma normal");
        assertEquals("Hemograma normal", r.getResultado());
        r.setObservaciones("Sin alteraciones");
        assertEquals("Sin alteraciones", r.getObservaciones());
        r.setDisponible(false);
        assertFalse(r.getDisponible());
        r.setFichaId(5L);
        assertEquals(5L, r.getFichaId());
        LocalDateTime ahora = LocalDateTime.now();
        r.setFechaRegistro(ahora);
        assertEquals(ahora, r.getFechaRegistro());
    }
}
