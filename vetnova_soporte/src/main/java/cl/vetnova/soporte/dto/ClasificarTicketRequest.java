package cl.vetnova.soporte.dto;

public class ClasificarTicketRequest {
    private Long categoriaId;
    private String prioridad;

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
}
