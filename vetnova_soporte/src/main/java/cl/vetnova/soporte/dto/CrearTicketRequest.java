package cl.vetnova.soporte.dto;

public class CrearTicketRequest {
    private Long clienteId;
    private String motivo;
    private String descripcion;
    private String sucursalId;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getSucursalId() { return sucursalId; }
    public void setSucursalId(String sucursalId) { this.sucursalId = sucursalId; }
}
