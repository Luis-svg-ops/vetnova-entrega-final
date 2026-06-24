package cl.vetnova.reportes.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Tablero de indicadores (clase del modelo del diagrama). Agrupa los KPIs
 * principales de una sucursal; no se persiste, se arma a demanda.
 */
public class Dashboard {

    private String sucursal;
    private Integer citasHoy;
    private Integer ordenesHoy;
    private Double ventasHoy;
    private Integer alertasStock;
    private Integer ticketsAbiertos;
    private Map<String, Object> indicadoresPrincipales = new HashMap<>();

    public Dashboard() {
    }

    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }

    public Integer getCitasHoy() { return citasHoy; }
    public void setCitasHoy(Integer citasHoy) { this.citasHoy = citasHoy; }

    public Integer getOrdenesHoy() { return ordenesHoy; }
    public void setOrdenesHoy(Integer ordenesHoy) { this.ordenesHoy = ordenesHoy; }

    public Double getVentasHoy() { return ventasHoy; }
    public void setVentasHoy(Double ventasHoy) { this.ventasHoy = ventasHoy; }

    public Integer getAlertasStock() { return alertasStock; }
    public void setAlertasStock(Integer alertasStock) { this.alertasStock = alertasStock; }

    public Integer getTicketsAbiertos() { return ticketsAbiertos; }
    public void setTicketsAbiertos(Integer ticketsAbiertos) { this.ticketsAbiertos = ticketsAbiertos; }

    public Map<String, Object> getIndicadoresPrincipales() { return indicadoresPrincipales; }
    public void setIndicadoresPrincipales(Map<String, Object> indicadoresPrincipales) { this.indicadoresPrincipales = indicadoresPrincipales; }
}
