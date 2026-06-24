package cl.vetnova.fichaclinica.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

public class RecetaTest {

    @Test
    void testReceta() {
        Receta receta = new Receta();
        receta.setId(1L);
        assertEquals(1L, receta.getId());
        receta.setFichaId(1L);
        assertEquals(1L, receta.getFichaId());
        receta.setVeterinarioId(1L);
        assertEquals(1L, receta.getVeterinarioId());
        receta.setMascotaId(1L);
        assertEquals(1L, receta.getMascotaId());
        receta.setMedicamentos("x");
        assertEquals("x", receta.getMedicamentos());
        receta.setDosis("x");
        assertEquals("x", receta.getDosis());
        receta.setFrecuencia("x");
        assertEquals("x", receta.getFrecuencia());
        receta.setDuracion("x");
        assertEquals("x", receta.getDuracion());
        receta.setIndicaciones("x");
        assertEquals("x", receta.getIndicaciones());
        receta.setFechaEmision(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), receta.getFechaEmision());
        receta.setFechaVencimiento(Date.valueOf("2025-02-01"));
        assertEquals(Date.valueOf("2025-02-01"), receta.getFechaVencimiento());
    }

}