package cl.vetnova.reportes.dto;

import java.time.LocalDate;

public class ReporteRequest {
    private String tipo;
    private String sucursal;
    private LocalDate desde;
    private LocalDate hasta;
    private Long generadoPor;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }
    public Long getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(Long generadoPor) { this.generadoPor = generadoPor; }
}
