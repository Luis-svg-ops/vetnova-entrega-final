package cl.vetnova.facturacion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ReporteRequestTest {

    @Test
    void testGettersYSetters() {
        ReporteRequest r = new ReporteRequest();
        r.setSucursal("CHILLAN");
        r.setPeriodo("2025-06");
        assertEquals("CHILLAN", r.getSucursal());
        assertEquals("2025-06", r.getPeriodo());
    }
}
