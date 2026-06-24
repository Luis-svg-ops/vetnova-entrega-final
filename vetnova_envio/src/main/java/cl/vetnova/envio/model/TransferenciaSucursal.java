package cl.vetnova.envio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias_sucursal")
public class TransferenciaSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idProducto;

    @Column(nullable = false)
    private Long idSucursalOrigen;

    @Column(nullable = false)
    private Long idSucursalDestino;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 20)
    private String estado = "COMPLETADA";

    @Column(length = 200)
    private String observacion;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public TransferenciaSucursal() {
    }

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
