package cl.vetnova.laboratorio.dto;

public class CrearProcesamientoRequest {
    private Long muestraId;
    private Long tecnicoId;
    private String metodologia;
    private String observaciones;

    public Long getMuestraId() { return muestraId; }
    public void setMuestraId(Long muestraId) { this.muestraId = muestraId; }
    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }
    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
