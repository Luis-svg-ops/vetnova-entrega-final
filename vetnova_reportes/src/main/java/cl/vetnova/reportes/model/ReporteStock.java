package cl.vetnova.reportes.model;

import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "reportes_stock")
public class ReporteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporte_id")
    private Long reporteId;

    @Column(name = "productos_con_stock_critico")
    private Integer productosConStockCritico;

    @Column(name = "productos_en_transito")
    private Integer productosEnTransito;

    @ElementCollection
    @CollectionTable(name = "stock_por_sucursal", joinColumns = @JoinColumn(name = "reporte_stock_id"))
    @MapKeyColumn(name = "sucursal")
    @Column(name = "cantidad")
    private Map<String, Integer> stockPorSucursal;

    @ElementCollection
    @CollectionTable(name = "movimientos_recientes", joinColumns = @JoinColumn(name = "reporte_stock_id"))
    @MapKeyColumn(name = "producto")
    @Column(name = "movimiento")
    private Map<String, Integer> movimientosRecientes;

    public ReporteStock() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReporteId() { return reporteId; }
    public void setReporteId(Long reporteId) { this.reporteId = reporteId; }

    public Integer getProductosConStockCritico() { return productosConStockCritico; }
    public void setProductosConStockCritico(Integer productosConStockCritico) { this.productosConStockCritico = productosConStockCritico; }

    public Integer getProductosEnTransito() { return productosEnTransito; }
    public void setProductosEnTransito(Integer productosEnTransito) { this.productosEnTransito = productosEnTransito; }

    public Map<String, Integer> getStockPorSucursal() { return stockPorSucursal; }
    public void setStockPorSucursal(Map<String, Integer> stockPorSucursal) { this.stockPorSucursal = stockPorSucursal; }

    public Map<String, Integer> getMovimientosRecientes() { return movimientosRecientes; }
    public void setMovimientosRecientes(Map<String, Integer> movimientosRecientes) { this.movimientosRecientes = movimientosRecientes; }
}
