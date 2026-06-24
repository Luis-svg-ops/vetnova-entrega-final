package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RegistrarResultadoRequestTest {

    @Test
    void testGettersYSetters() {
        RegistrarResultadoRequest r = new RegistrarResultadoRequest();
        r.setOrdenExamenId(1L);
        r.setMuestraId(2L);
        r.setTecnicoId(3L);
        r.setResultado("Hemograma normal");
        r.setObservaciones("Sin alteraciones");
        assertEquals(1L, r.getOrdenExamenId());
        assertEquals(2L, r.getMuestraId());
        assertEquals(3L, r.getTecnicoId());
        assertEquals("Hemograma normal", r.getResultado());
        assertEquals("Sin alteraciones", r.getObservaciones());
    }
}
