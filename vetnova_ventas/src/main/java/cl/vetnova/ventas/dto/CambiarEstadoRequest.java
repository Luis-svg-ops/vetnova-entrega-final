package cl.vetnova.ventas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CambiarEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PENDIENTE|CONFIRMADA|ENVIADA|ENTREGADA|CANCELADA",
             message = "Estado inválido: usar PENDIENTE, CONFIRMADA, ENVIADA, ENTREGADA o CANCELADA")
    private String estado;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
