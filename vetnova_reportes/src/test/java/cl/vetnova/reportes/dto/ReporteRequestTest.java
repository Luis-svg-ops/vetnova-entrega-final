package cl.vetnova.reportes.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ReporteRequestTest {

    @Test
    void testGettersYSetters() {
        ReporteRequest r = new ReporteRequest();
        r.setTipo("VENTA");
        r.setSucursal(1L);
        LocalDate desde = LocalDate.of(2025, 1, 1);
        r.setDesde(desde);
        LocalDate hasta = LocalDate.of(2025, 6, 1);
        r.setHasta(hasta);
        r.setGeneradoPor(2L);
        assertEquals("VENTA", r.getTipo());
        assertEquals(1L, r.getSucursal());
        assertEquals(desde, r.getDesde());
        assertEquals(hasta, r.getHasta());
        assertEquals(2L, r.getGeneradoPor());
    }
}
