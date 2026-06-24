package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.model.Dashboard;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    public Dashboard cargarIndicadores(Long sucursal) {
        if (sucursal == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        // La existencia de la sucursal vive en MS Auth/Sucursales → verificación diferida.
        // Los KPIs reales (citas, órdenes, ventas, alertas, tickets) son cross-service → diferidos;
        // sin actividad consolidada se devuelven en cero (caso "sin datos" del CA).
        Dashboard dashboard = new Dashboard();
        dashboard.setSucursal(String.valueOf(sucursal));
        dashboard.setCitasHoy(0);
        dashboard.setOrdenesHoy(0);
        dashboard.setVentasHoy(0.0);
        dashboard.setAlertasStock(0);
        dashboard.setTicketsAbiertos(0);
        Map<String, Object> indicadores = new LinkedHashMap<>();
        indicadores.put("citasHoy", dashboard.getCitasHoy());
        indicadores.put("ordenesHoy", dashboard.getOrdenesHoy());
        indicadores.put("ventasHoy", dashboard.getVentasHoy());
        indicadores.put("alertasStock", dashboard.getAlertasStock());
        indicadores.put("ticketsAbiertos", dashboard.getTicketsAbiertos());
        dashboard.setIndicadoresPrincipales(indicadores);
        return dashboard;
    }
}
