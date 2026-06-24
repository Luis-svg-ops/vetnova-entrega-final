package cl.vetnova.inventario.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transferencias_stock")
public class TransferenciaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "sucursal_origen", length = 150)
    private String sucursalOrigen;

    @Column(name = "sucursal_destino", length = 150)
    private String sucursalDestino;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    public TransferenciaStock() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getSucursalOrigen() { return sucursalOrigen; }
    public void setSucursalOrigen(String sucursalOrigen) { this.sucursalOrigen = sucursalOrigen; }

    public String getSucursalDestino() { return sucursalDestino; }
    public void setSucursalDestino(String sucursalDestino) { this.sucursalDestino = sucursalDestino; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }
}
