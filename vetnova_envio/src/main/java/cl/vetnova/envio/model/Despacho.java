package cl.vetnova.envio.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "despachos")
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id")
    private Long ordenId;

    @Column(name = "sucursal_origen", length = 150)
    private String sucursalOrigen;

    @Column(name = "sucursal_destino", length = 150)
    private String sucursalDestino;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "responsable", length = 120)
    private String responsable;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_estimada")
    private LocalDateTime fechaEstimada;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    public Despacho() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public String getSucursalOrigen() { return sucursalOrigen; }
    public void setSucursalOrigen(String sucursalOrigen) { this.sucursalOrigen = sucursalOrigen; }

    public String getSucursalDestino() { return sucursalDestino; }
    public void setSucursalDestino(String sucursalDestino) { this.sucursalDestino = sucursalDestino; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaEstimada() { return fechaEstimada; }
    public void setFechaEstimada(LocalDateTime fechaEstimada) { this.fechaEstimada = fechaEstimada; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
}
