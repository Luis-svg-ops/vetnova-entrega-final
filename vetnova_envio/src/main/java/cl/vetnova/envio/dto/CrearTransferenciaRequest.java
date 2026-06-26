package cl.vetnova.envio.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CrearTransferenciaRequest {

    @NotNull(message = "El id del producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "La sucursal de origen es obligatoria")
    private String idSucursalOrigen;

    @NotNull(message = "La sucursal de destino es obligatoria")
    private String idSucursalDestino;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    private String observacion;

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public String getIdSucursalOrigen() { return idSucursalOrigen; }
    public void setIdSucursalOrigen(String idSucursalOrigen) { this.idSucursalOrigen = idSucursalOrigen; }
    public String getIdSucursalDestino() { return idSucursalDestino; }
    public void setIdSucursalDestino(String idSucursalDestino) { this.idSucursalDestino = idSucursalDestino; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
