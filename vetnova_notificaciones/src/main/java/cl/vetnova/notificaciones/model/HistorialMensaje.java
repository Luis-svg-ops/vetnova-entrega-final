package cl.vetnova.notificaciones.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "historial_mensaje")
public class HistorialMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notificacion_id")
    private Long notificacionId;

    @Column(name = "canal_id")
    private Long canalId;

    @Column(name = "estado", length = 30)
    private String estado;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    public HistorialMensaje() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNotificacionId() { return notificacionId; }
    public void setNotificacionId(Long notificacionId) { this.notificacionId = notificacionId; }

    public Long getCanalId() { return canalId; }
    public void setCanalId(Long canalId) { this.canalId = canalId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}
