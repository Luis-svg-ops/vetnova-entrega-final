package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class RespaldoTest {

    @Test
    void testGettersYSetters() {
        Respaldo r = new Respaldo();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setTipo("COMPLETO");
        assertEquals("COMPLETO", r.getTipo());
        r.setAlcance("TOTAL");
        assertEquals("TOTAL", r.getAlcance());
        r.setUbicacion("/backups/2025");
        assertEquals("/backups/2025", r.getUbicacion());
        r.setTamanoBytes(1024L);
        assertEquals(1024L, r.getTamanoBytes());
        r.setEstado("EN_CURSO");
        assertEquals("EN_CURSO", r.getEstado());
        r.setEjecutadoPor(1L);
        assertEquals(1L, r.getEjecutadoPor());
        LocalDateTime ahora = LocalDateTime.now();
        r.setFechaInicio(ahora);
        assertEquals(ahora, r.getFechaInicio());
        r.setFechaFin(ahora);
        assertEquals(ahora, r.getFechaFin());
    }
}
