package cl.vetnova.reportes.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "incidentes_sistema")
public class IncidenteSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "microservicio", length = 100)
    private String microservicio;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "severidad", length = 50)
    private String severidad;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "notificado")
    private Boolean notificado = false;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "fecha_deteccion")
    private LocalDateTime fechaDeteccion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public IncidenteSistema() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMicroservicio() { return microservicio; }
    public void setMicroservicio(String microservicio) { this.microservicio = microservicio; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getSeveridad() { return severidad; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getNotificado() { return notificado; }
    public void setNotificado(Boolean notificado) { this.notificado = notificado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaDeteccion() { return fechaDeteccion; }
    public void setFechaDeteccion(LocalDateTime fechaDeteccion) { this.fechaDeteccion = fechaDeteccion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}
