package cl.vetnova.inventario.dto;

public class StockDisponibleResponse {

    private Long idProducto;
    private Long idSucursal;
    private Integer cantidadDisponible;

    public StockDisponibleResponse() {
    }

    public StockDisponibleResponse(Long idProducto, Long idSucursal, Integer cantidadDisponible) {
        this.idProducto = idProducto;
        this.idSucursal = idSucursal;
        this.cantidadDisponible = cantidadDisponible;
    }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public Long getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Long idSucursal) { this.idSucursal = idSucursal; }
    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
}
