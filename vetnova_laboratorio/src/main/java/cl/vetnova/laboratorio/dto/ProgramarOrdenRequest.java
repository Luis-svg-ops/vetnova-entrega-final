package cl.vetnova.laboratorio.dto;

import java.time.LocalDateTime;

public class ProgramarOrdenRequest {
    private LocalDateTime fechaProgramada;

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }
}
