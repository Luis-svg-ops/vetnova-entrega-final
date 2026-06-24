package cl.vetnova.inventario.dto;

public class SalidaStockRequest {

    private Integer cantidad;
    private String motivo;

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
