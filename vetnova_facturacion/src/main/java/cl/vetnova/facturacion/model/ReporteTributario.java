package cl.vetnova.facturacion.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_tributarios")
public class ReporteTributario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sucursal", length = 30)
    private String sucursal;

    @Column(name = "periodo", length = 20)
    private String periodo;

    @Column(name = "total_documentos")
    private Integer totalDocumentos;

    @Column(name = "monto_neto")
    private Double montoNeto;

    @Column(name = "monto_iva")
    private Double montoIva;

    @Column(name = "monto_total")
    private Double montoTotal;

    @Column(name = "generado_en")
    private LocalDateTime generadoEn;

    public ReporteTributario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public Integer getTotalDocumentos() { return totalDocumentos; }
    public void setTotalDocumentos(Integer totalDocumentos) { this.totalDocumentos = totalDocumentos; }

    public Double getMontoNeto() { return montoNeto; }
    public void setMontoNeto(Double montoNeto) { this.montoNeto = montoNeto; }

    public Double getMontoIva() { return montoIva; }
    public void setMontoIva(Double montoIva) { this.montoIva = montoIva; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(LocalDateTime generadoEn) { this.generadoEn = generadoEn; }
}
