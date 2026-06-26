package cl.vetnova.inventario.dto;

public class StockSucursalResponse {

    private String idSucursal;
    private Integer cantidad;
    private Integer stockMinimo;
    private boolean critico;

    public String getIdSucursal() { return idSucursal; }
    public void setIdSucursal(String idSucursal) { this.idSucursal = idSucursal; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public boolean isCritico() { return critico; }
    public void setCritico(boolean critico) { this.critico = critico; }
}
