package cl.vetnova.facturacion.dto;

public class FolioRequest {
    private Long sucursal;
    private String tipoDocumento;
    private Integer folioDesde;
    private Integer folioHasta;

    public Long getSucursal() { return sucursal; }
    public void setSucursal(Long sucursal) { this.sucursal = sucursal; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public Integer getFolioDesde() { return folioDesde; }
    public void setFolioDesde(Integer folioDesde) { this.folioDesde = folioDesde; }
    public Integer getFolioHasta() { return folioHasta; }
    public void setFolioHasta(Integer folioHasta) { this.folioHasta = folioHasta; }
}
