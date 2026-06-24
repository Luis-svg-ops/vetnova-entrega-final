package cl.vetnova.laboratorio.dto;

import java.time.LocalDateTime;

public class CrearOrdenExamenRequest {
    private Long mascotaId;
    private Long veterinarioId;
    private Long tipoExamenId;
    private String descripcion;
    private LocalDateTime fechaProgramada;

    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }
    public Long getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Long veterinarioId) { this.veterinarioId = veterinarioId; }
    public Long getTipoExamenId() { return tipoExamenId; }
    public void setTipoExamenId(Long tipoExamenId) { this.tipoExamenId = tipoExamenId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }
}
