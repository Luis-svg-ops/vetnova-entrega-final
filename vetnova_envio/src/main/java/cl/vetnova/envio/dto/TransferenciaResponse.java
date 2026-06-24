package cl.vetnova.envio.dto;

import java.time.LocalDateTime;

public class TransferenciaResponse {

    private Long id;
    private Long idProducto;
    private Long idSucursalOrigen;
    private Long idSucursalDestino;
    private Integer cantidad;
    private String estado;
    private String observacion;
    private LocalDateTime fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public Long getIdSucursalOrigen() { return idSucursalOrigen; }
    public void setIdSucursalOrigen(Long idSucursalOrigen) { this.idSucursalOrigen = idSucursalOrigen; }
    public Long getIdSucursalDestino() { return idSucursalDestino; }
    public void setIdSucursalDestino(Long idSucursalDestino) { this.idSucursalDestino = idSucursalDestino; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
