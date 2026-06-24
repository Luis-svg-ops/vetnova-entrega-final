package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class DashboardTest {

    @Test
    void testGettersYSetters() {
        Dashboard d = new Dashboard();
        d.setSucursal("1");
        assertEquals("1", d.getSucursal());
        d.setCitasHoy(5);
        assertEquals(5, d.getCitasHoy());
        d.setOrdenesHoy(3);
        assertEquals(3, d.getOrdenesHoy());
        d.setVentasHoy(3000.0);
        assertEquals(3000.0, d.getVentasHoy());
        d.setAlertasStock(4);
        assertEquals(4, d.getAlertasStock());
        d.setTicketsAbiertos(6);
        assertEquals(6, d.getTicketsAbiertos());
        Map<String, Object> ind = Map.of("citasHoy", 5);
        d.setIndicadoresPrincipales(ind);
        assertEquals(ind, d.getIndicadoresPrincipales());
    }
}
