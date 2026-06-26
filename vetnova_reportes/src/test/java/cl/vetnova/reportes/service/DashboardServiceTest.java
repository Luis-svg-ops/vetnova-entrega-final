package cl.vetnova.reportes.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.model.Dashboard;

public class DashboardServiceTest {

    private final DashboardService service = new DashboardService();

    @Test
    void testCargarIndicadoresSucursalNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cargarIndicadores(null));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testCargarIndicadoresCasoFeliz() {
        Dashboard d = service.cargarIndicadores("CHILLAN");
        assertEquals("CHILLAN", d.getSucursal());
        assertEquals(0, d.getCitasHoy());
        assertEquals(0, d.getOrdenesHoy());
        assertEquals(0.0, d.getVentasHoy());
        assertEquals(0, d.getAlertasStock());
        assertEquals(0, d.getTicketsAbiertos());
        assertTrue(d.getIndicadoresPrincipales().containsKey("citasHoy"));
        assertTrue(d.getIndicadoresPrincipales().containsKey("ticketsAbiertos"));
    }
}
