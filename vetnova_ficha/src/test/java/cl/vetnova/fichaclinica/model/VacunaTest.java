package cl.vetnova.fichaclinica.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

public class VacunaTest {

    @Test
    void testVacuna() {
        Vacuna vacuna = new Vacuna();
        vacuna.setId(1L);
        assertEquals(1L, vacuna.getId());
        vacuna.setFichaId(1L);
        assertEquals(1L, vacuna.getFichaId());
        vacuna.setVeterinarioId(1L);
        assertEquals(1L, vacuna.getVeterinarioId());
        vacuna.setNombre("x");
        assertEquals("x", vacuna.getNombre());
        vacuna.setLote("x");
        assertEquals("x", vacuna.getLote());
        vacuna.setFechaAplicacion(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), vacuna.getFechaAplicacion());
        vacuna.setFechaProximaDosis(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), vacuna.getFechaProximaDosis());
        vacuna.setObservaciones("x");
        assertEquals("x", vacuna.getObservaciones());
    }

}