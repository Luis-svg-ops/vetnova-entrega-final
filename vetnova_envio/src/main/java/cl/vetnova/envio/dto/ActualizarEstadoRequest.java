package cl.vetnova.envio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ActualizarEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PREPARANDO|EN_RUTA|ENTREGADO|CANCELADO",
             message = "Estado inválido: usar PREPARANDO, EN_RUTA, ENTREGADO o CANCELADO")
    private String estado;

    private String observacion;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
