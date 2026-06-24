package cl.vetnova.laboratorio.dto;

public class RegistrarMuestraRequest {
    private Long ordenExamenId;
    private String tipo;
    private String codigoMuestra;
    private String descripcion;

    public Long getOrdenExamenId() { return ordenExamenId; }
    public void setOrdenExamenId(Long ordenExamenId) { this.ordenExamenId = ordenExamenId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCodigoMuestra() { return codigoMuestra; }
    public void setCodigoMuestra(String codigoMuestra) { this.codigoMuestra = codigoMuestra; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
