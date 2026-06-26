package cl.vetnova.ventas.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrdenResponse {

    private Long id;
    private Long clienteId;
    private String sucursal;
    private String estado;
    private Double subtotal;
    private Double impuestos;
    private Double total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaConfirmacion;
    private List<DetalleOrdenResponse> detalles;
    private List<PagoResponse> pagos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    public Double getImpuestos() { return impuestos; }
    public void setImpuestos(Double impuestos) { this.impuestos = impuestos; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }
    public List<DetalleOrdenResponse> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrdenResponse> detalles) { this.detalles = detalles; }
    public List<PagoResponse> getPagos() { return pagos; }
    public void setPagos(List<PagoResponse> pagos) { this.pagos = pagos; }
}
