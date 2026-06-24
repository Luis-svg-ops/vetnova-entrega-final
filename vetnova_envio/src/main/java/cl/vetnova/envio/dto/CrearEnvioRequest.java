package cl.vetnova.envio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CrearEnvioRequest {

    @NotNull(message = "El id de la orden es obligatorio")
    private Long ordenId;

    @NotBlank(message = "El tipo de envío es obligatorio")
    @Pattern(regexp = "DOMICILIO|RETIRO_TIENDA", message = "El tipo debe ser DOMICILIO o RETIRO_TIENDA")
    private String tipoEnvio;

    @NotNull(message = "La sucursal de origen es obligatoria")
    private Long idSucursalOrigen;

    private String direccionEntrega;

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }
    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }
    public Long getIdSucursalOrigen() { return idSucursalOrigen; }
    public void setIdSucursalOrigen(Long idSucursalOrigen) { this.idSucursalOrigen = idSucursalOrigen; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
}
