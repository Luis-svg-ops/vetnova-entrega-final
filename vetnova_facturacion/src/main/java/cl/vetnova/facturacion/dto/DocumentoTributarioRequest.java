package cl.vetnova.facturacion.dto;

public class DocumentoTributarioRequest {
    private Long ordenId;
    private Long clienteId;
    private String tipo;
    private Double neto;
    private Double total;
    private String rutEmisor;
    private String rutReceptor;
    private Long sucursal;

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getNeto() { return neto; }
    public void setNeto(Double neto) { this.neto = neto; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getRutEmisor() { return rutEmisor; }
    public void setRutEmisor(String rutEmisor) { this.rutEmisor = rutEmisor; }
    public String getRutReceptor() { return rutReceptor; }
    public void setRutReceptor(String rutReceptor) { this.rutReceptor = rutReceptor; }
    public Long getSucursal() { return sucursal; }
    public void setSucursal(Long sucursal) { this.sucursal = sucursal; }
}
