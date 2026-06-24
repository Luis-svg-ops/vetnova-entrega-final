package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CrearProcesamientoRequestTest {

    @Test
    void testGettersYSetters() {
        CrearProcesamientoRequest r = new CrearProcesamientoRequest();
        r.setMuestraId(1L);
        r.setTecnicoId(3L);
        r.setMetodologia("Citometría de flujo");
        r.setObservaciones("Sin alteraciones");
        assertEquals(1L, r.getMuestraId());
        assertEquals(3L, r.getTecnicoId());
        assertEquals("Citometría de flujo", r.getMetodologia());
        assertEquals("Sin alteraciones", r.getObservaciones());
    }
}
