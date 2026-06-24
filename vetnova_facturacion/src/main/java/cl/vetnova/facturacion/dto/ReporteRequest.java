package cl.vetnova.facturacion.dto;

public class ReporteRequest {
    private Long sucursal;
    private String periodo;

    public Long getSucursal() { return sucursal; }
    public void setSucursal(Long sucursal) { this.sucursal = sucursal; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
}
