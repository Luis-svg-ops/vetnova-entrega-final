package cl.vetnova.soporte.dto;

public class DerivacionRequest {
    private Long ticketId;
    private Long responsableNuevo;
    private String motivo;

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getResponsableNuevo() { return responsableNuevo; }
    public void setResponsableNuevo(Long responsableNuevo) { this.responsableNuevo = responsableNuevo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
