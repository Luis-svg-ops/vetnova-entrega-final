package cl.vetnova.laboratorio.dto;

import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.model.TipoExamen;
import java.time.LocalDateTime;

public class OrdenExamenResponse {

    private Long id;
    private Long mascotaId;
    private String nombreMascota;
    private Long veterinarioId;
    private TipoExamen tipoExamen;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaProgramada;
    private String motivoCancelacion;

    public OrdenExamenResponse(OrdenExamen o, String nombreMascota) {
        this.id = o.getId();
        this.mascotaId = o.getMascotaId();
        this.nombreMascota = nombreMascota;
        this.veterinarioId = o.getVeterinarioId();
        this.tipoExamen = o.getTipoExamen();
        this.descripcion = o.getDescripcion();
        this.estado = o.getEstado();
        this.fechaSolicitud = o.getFechaSolicitud();
        this.fechaProgramada = o.getFechaProgramada();
        this.motivoCancelacion = o.getMotivoCancelacion();
    }

    public Long getId() { return id; }
    public Long getMascotaId() { return mascotaId; }
    public String getNombreMascota() { return nombreMascota; }
    public Long getVeterinarioId() { return veterinarioId; }
    public TipoExamen getTipoExamen() { return tipoExamen; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
}
