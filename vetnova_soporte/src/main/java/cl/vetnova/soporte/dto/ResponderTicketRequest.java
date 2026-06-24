package cl.vetnova.soporte.dto;

public class ResponderTicketRequest {
    private Long autorId;
    private String contenido;
    private Boolean visible;

    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }
}
