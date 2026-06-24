package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class CompletarProcesamientoRequestTest {

    @Test
    void testGettersYSetters() {
        CompletarProcesamientoRequest r = new CompletarProcesamientoRequest();
        LocalDateTime f = LocalDateTime.now();
        r.setFechaFin(f);
        r.setObservaciones("Completado");
        assertEquals(f, r.getFechaFin());
        assertEquals("Completado", r.getObservaciones());
    }
}
