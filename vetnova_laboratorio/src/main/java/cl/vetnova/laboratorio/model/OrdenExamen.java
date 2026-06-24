package cl.vetnova.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes_examen")
public class OrdenExamen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "mascota_id")
    private Long mascotaId;
    @Column(name = "veterinario_id")
    private Long veterinarioId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_examen_id")
    private TipoExamen tipoExamen;
    @Column(name = "descripcion", length = 700)
    private String descripcion;
    @Column(name = "estado", length = 30)
    private String estado;
    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;
    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    public OrdenExamen() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }
    public Long getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Long veterinarioId) { this.veterinarioId = veterinarioId; }
    public TipoExamen getTipoExamen() { return tipoExamen; }
    public void setTipoExamen(TipoExamen tipoExamen) { this.tipoExamen = tipoExamen; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
}
