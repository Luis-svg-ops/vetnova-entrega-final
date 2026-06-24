package cl.vetnova.inventario.dto;

public class StockSucursalResponse {

    private Long idSucursal;
    private Integer cantidad;
    private Integer stockMinimo;
    private boolean critico;

    public Long getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Long idSucursal) { this.idSucursal = idSucursal; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public boolean isCritico() { return critico; }
    public void setCritico(boolean critico) { this.critico = critico; }
}
