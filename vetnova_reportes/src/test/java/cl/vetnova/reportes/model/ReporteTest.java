package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ReporteTest {

    @Test
    void testGettersYSetters() {
        Reporte r = new Reporte();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setTipo("VENTA");
        assertEquals("VENTA", r.getTipo());
        r.setSucursal("CHILLAN");
        assertEquals("CHILLAN", r.getSucursal());
        LocalDate desde = LocalDate.of(2025, 1, 1);
        r.setDesde(desde);
        assertEquals(desde, r.getDesde());
        LocalDate hasta = LocalDate.of(2025, 6, 1);
        r.setHasta(hasta);
        assertEquals(hasta, r.getHasta());
        r.setGeneradoPor(2L);
        assertEquals(2L, r.getGeneradoPor());
        LocalDateTime ahora = LocalDateTime.now();
        r.setGeneradoEn(ahora);
        assertEquals(ahora, r.getGeneradoEn());
        r.setEstado("GENERADO");
        assertEquals("GENERADO", r.getEstado());
    }
}
