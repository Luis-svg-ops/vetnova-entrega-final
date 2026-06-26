package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.model.Dashboard;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    public Dashboard cargarIndicadores(String sucursal) {
        if (sucursal == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        Dashboard dashboard = new Dashboard();
        dashboard.setSucursal(sucursal);
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
