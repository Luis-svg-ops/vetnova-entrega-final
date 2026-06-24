package cl.vetnova.inventario.dto;

public class ResolucionRequest {

    private Long aprobadoPor;
    private String motivo;

    public Long getAprobadoPor() { return aprobadoPor; }
    public void setAprobadoPor(Long aprobadoPor) { this.aprobadoPor = aprobadoPor; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
