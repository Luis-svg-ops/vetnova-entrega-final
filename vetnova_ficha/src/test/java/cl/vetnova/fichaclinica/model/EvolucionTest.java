package cl.vetnova.fichaclinica.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class EvolucionTest {

    @Test
    void testEvolucion() {
        Evolucion evolucion = new Evolucion();
        evolucion.setId(1L);
        assertEquals(1L, evolucion.getId());
        evolucion.setFichaId(1L);
        assertEquals(1L, evolucion.getFichaId());
        evolucion.setVeterinarioId(1L);
        assertEquals(1L, evolucion.getVeterinarioId());
        evolucion.setCitaId(1L);
        assertEquals(1L, evolucion.getCitaId());
        evolucion.setFecha(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), evolucion.getFecha());
        evolucion.setAnamnesis("x");
        assertEquals("x", evolucion.getAnamnesis());
        evolucion.setExamenFisico("x");
        assertEquals("x", evolucion.getExamenFisico());
        evolucion.setDiagnostico("x");
        assertEquals("x", evolucion.getDiagnostico());
        evolucion.setTratamiento("x");
        assertEquals("x", evolucion.getTratamiento());
        evolucion.setObservaciones("x");
        assertEquals("x", evolucion.getObservaciones());
        evolucion.setDescripcion("Paciente estable");
        assertEquals("Paciente estable", evolucion.getDescripcion());
        LocalDateTime t = LocalDateTime.now();
        evolucion.setFechaRegistro(t);
        assertEquals(t, evolucion.getFechaRegistro());
    }

}