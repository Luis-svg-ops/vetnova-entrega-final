package cl.vetnova.soporte.dto;

public class PromedioValoracionResponse {
    private Long sucursalId;
    private Double promedio;
    private long total;

    public PromedioValoracionResponse() {}

    public PromedioValoracionResponse(Long sucursalId, Double promedio, long total) {
        this.sucursalId = sucursalId;
        this.promedio = promedio;
        this.total = total;
    }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }
    public Double getPromedio() { return promedio; }
    public void setPromedio(Double promedio) { this.promedio = promedio; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
