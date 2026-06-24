package cl.vetnova.laboratorio.dto;

public class TipoExamenRequest {
    private String nombre;
    private String descripcion;
    private Integer tiempoEstimadoHoras;
    private Boolean requiereMuestra;
    private String instrucciones;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getTiempoEstimadoHoras() { return tiempoEstimadoHoras; }
    public void setTiempoEstimadoHoras(Integer tiempoEstimadoHoras) { this.tiempoEstimadoHoras = tiempoEstimadoHoras; }
    public Boolean getRequiereMuestra() { return requiereMuestra; }
    public void setRequiereMuestra(Boolean requiereMuestra) { this.requiereMuestra = requiereMuestra; }
    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }
}
