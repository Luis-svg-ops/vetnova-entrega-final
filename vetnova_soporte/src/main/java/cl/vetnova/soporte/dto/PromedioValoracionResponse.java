package cl.vetnova.soporte.dto;

public class PromedioValoracionResponse {
    private String sucursalId;
    private Double promedio;
    private long total;

    public PromedioValoracionResponse() {}

    public PromedioValoracionResponse(String sucursalId, Double promedio, long total) {
        this.sucursalId = sucursalId;
        this.promedio = promedio;
        this.total = total;
    }

    public String getSucursalId() { return sucursalId; }
    public void setSucursalId(String sucursalId) { this.sucursalId = sucursalId; }
    public Double getPromedio() { return promedio; }
    public void setPromedio(Double promedio) { this.promedio = promedio; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
