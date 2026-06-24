package cl.vetnova.inventario.dto;

public class EntradaStockRequest {

    private Integer cantidad;
    private String responsable;

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
}
