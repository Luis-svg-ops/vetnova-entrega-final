package cl.vetnova.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CrearOrdenRequest {

    @NotNull(message = "El id del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "La sucursal es obligatoria")
    private String sucursal;

    @NotEmpty(message = "La orden debe tener al menos un detalle")
    @Valid
    private List<DetalleOrdenRequest> detalles;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
    public List<DetalleOrdenRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrdenRequest> detalles) { this.detalles = detalles; }
}
