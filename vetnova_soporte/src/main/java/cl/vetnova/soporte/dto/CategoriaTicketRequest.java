package cl.vetnova.soporte.dto;

public class CategoriaTicketRequest {
    private String nombre;
    private String descripcion;
    private String areaPorDefecto;
    private String prioridadDefault;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getAreaPorDefecto() { return areaPorDefecto; }
    public void setAreaPorDefecto(String areaPorDefecto) { this.areaPorDefecto = areaPorDefecto; }
    public String getPrioridadDefault() { return prioridadDefault; }
    public void setPrioridadDefault(String prioridadDefault) { this.prioridadDefault = prioridadDefault; }
}
