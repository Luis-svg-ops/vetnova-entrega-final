package cl.vetnova.reportes.model;

import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "reportes_venta")
public class ReporteVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporte_id")
    private Long reporteId;

    @Column(name = "total_ordenes")
    private Integer totalOrdenes;

    @Column(name = "monto_total")
    private Double montoTotal;

    @Column(name = "productos_vendidos")
    private Integer productosVendidos;

    // producto -> monto vendido
    @ElementCollection
    @CollectionTable(name = "venta_por_producto", joinColumns = @JoinColumn(name = "reporte_venta_id"))
    @MapKeyColumn(name = "producto")
    @Column(name = "monto")
    private Map<String, Double> ventaPorProducto;

    // periodo (ej: "2025-01") -> monto total
    @ElementCollection
    @CollectionTable(name = "venta_por_periodo", joinColumns = @JoinColumn(name = "reporte_venta_id"))
    @MapKeyColumn(name = "periodo")
    @Column(name = "monto")
    private Map<String, Double> ventaPorPeriodo;

    public ReporteVenta() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReporteId() { return reporteId; }
    public void setReporteId(Long reporteId) { this.reporteId = reporteId; }

    public Integer getTotalOrdenes() { return totalOrdenes; }
    public void setTotalOrdenes(Integer totalOrdenes) { this.totalOrdenes = totalOrdenes; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public Integer getProductosVendidos() { return productosVendidos; }
    public void setProductosVendidos(Integer productosVendidos) { this.productosVendidos = productosVendidos; }

    public Map<String, Double> getVentaPorProducto() { return ventaPorProducto; }
    public void setVentaPorProducto(Map<String, Double> ventaPorProducto) { this.ventaPorProducto = ventaPorProducto; }

    public Map<String, Double> getVentaPorPeriodo() { return ventaPorPeriodo; }
    public void setVentaPorPeriodo(Map<String, Double> ventaPorPeriodo) { this.ventaPorPeriodo = ventaPorPeriodo; }
}
