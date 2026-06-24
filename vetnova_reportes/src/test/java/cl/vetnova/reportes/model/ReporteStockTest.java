package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class ReporteStockTest {

    @Test
    void testGettersYSetters() {
        ReporteStock r = new ReporteStock();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setReporteId(2L);
        assertEquals(2L, r.getReporteId());
        r.setProductosConStockCritico(3);
        assertEquals(3, r.getProductosConStockCritico());
        r.setProductosEnTransito(2);
        assertEquals(2, r.getProductosEnTransito());
        Map<String, Integer> porSuc = Map.of("1", 100, "2", 200);
        r.setStockPorSucursal(porSuc);
        assertEquals(porSuc, r.getStockPorSucursal());
        Map<String, Integer> movs = Map.of("ENTRADA", 8);
        r.setMovimientosRecientes(movs);
        assertEquals(movs, r.getMovimientosRecientes());
    }
}
