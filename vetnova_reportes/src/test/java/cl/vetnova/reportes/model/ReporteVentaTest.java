package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class ReporteVentaTest {

    @Test
    void testGettersYSetters() {
        ReporteVenta r = new ReporteVenta();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setReporteId(2L);
        assertEquals(2L, r.getReporteId());
        r.setTotalOrdenes(15);
        assertEquals(15, r.getTotalOrdenes());
        r.setMontoTotal(4500.0);
        assertEquals(4500.0, r.getMontoTotal());
        r.setProductosVendidos(10);
        assertEquals(10, r.getProductosVendidos());
        Map<String, Double> porProd = Map.of("10", 2000.0, "20", 2500.0);
        r.setVentaPorProducto(porProd);
        assertEquals(porProd, r.getVentaPorProducto());
        Map<String, Double> porPer = Map.of("2025-01", 1000.0);
        r.setVentaPorPeriodo(porPer);
        assertEquals(porPer, r.getVentaPorPeriodo());
    }
}
