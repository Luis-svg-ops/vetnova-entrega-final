package cl.vetnova.laboratorio.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ProcesamientoTest {

    @Test
    void testGettersYSetters() {
        Procesamiento p = new Procesamiento();
        p.setId(1L);
        assertEquals(1L, p.getId());
        p.setMuestraId(2L);
        assertEquals(2L, p.getMuestraId());
        p.setTecnicoId(3L);
        assertEquals(3L, p.getTecnicoId());
        p.setMetodologia("Citometría de flujo");
        assertEquals("Citometría de flujo", p.getMetodologia());
        p.setEstado("EN_PROCESO");
        assertEquals("EN_PROCESO", p.getEstado());
        LocalDateTime ahora = LocalDateTime.now();
        p.setFechaInicio(ahora);
        assertEquals(ahora, p.getFechaInicio());
        p.setFechaFin(ahora);
        assertEquals(ahora, p.getFechaFin());
        p.setObservaciones("Sin alteraciones");
        assertEquals("Sin alteraciones", p.getObservaciones());
    }
}
