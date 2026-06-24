package cl.vetnova.laboratorio.dto;

import java.time.LocalDateTime;

public class CompletarProcesamientoRequest {
    private LocalDateTime fechaFin;
    private String observaciones;

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
