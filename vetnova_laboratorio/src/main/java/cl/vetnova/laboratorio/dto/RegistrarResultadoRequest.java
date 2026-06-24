package cl.vetnova.laboratorio.dto;

public class RegistrarResultadoRequest {
    private Long ordenExamenId;
    private Long muestraId;
    private Long tecnicoId;
    private String resultado;
    private String observaciones;

    public Long getOrdenExamenId() { return ordenExamenId; }
    public void setOrdenExamenId(Long ordenExamenId) { this.ordenExamenId = ordenExamenId; }
    public Long getMuestraId() { return muestraId; }
    public void setMuestraId(Long muestraId) { this.muestraId = muestraId; }
    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
