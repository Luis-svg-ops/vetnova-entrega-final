package cl.vetnova.facturacion.dto;

public class ReporteRequest {
    private String sucursal;
    private String periodo;

    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
}
