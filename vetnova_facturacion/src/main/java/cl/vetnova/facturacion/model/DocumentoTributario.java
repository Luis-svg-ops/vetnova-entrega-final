package cl.vetnova.facturacion.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentos_tributarios")
public class DocumentoTributario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id")
    private Long ordenId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "folio", length = 20)
    private String folio;

    @Column(name = "neto")
    private Double neto;

    @Column(name = "iva")
    private Double iva;

    @Column(name = "total")
    private Double total;

    @Column(name = "estado_sii", length = 50)
    private String estadoSII;

    @Column(name = "rut_emisor", length = 20)
    private String rutEmisor;

    @Column(name = "rut_receptor", length = 20)
    private String rutReceptor;

    @Column(name = "sucursal")
    private Long sucursal;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    public DocumentoTributario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public Double getNeto() { return neto; }
    public void setNeto(Double neto) { this.neto = neto; }

    public Double getIva() { return iva; }
    public void setIva(Double iva) { this.iva = iva; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getEstadoSII() { return estadoSII; }
    public void setEstadoSII(String estadoSII) { this.estadoSII = estadoSII; }

    public String getRutEmisor() { return rutEmisor; }
    public void setRutEmisor(String rutEmisor) { this.rutEmisor = rutEmisor; }

    public String getRutReceptor() { return rutReceptor; }
    public void setRutReceptor(String rutReceptor) { this.rutReceptor = rutReceptor; }

    public Long getSucursal() { return sucursal; }
    public void setSucursal(Long sucursal) { this.sucursal = sucursal; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}
