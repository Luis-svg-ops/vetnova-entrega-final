package cl.vetnova.inventario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_sucursal",
       uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "id_sucursal"}))
public class StockSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "id_sucursal", nullable = false, length = 30)
    private String idSucursal;

    @Column(nullable = false)
    private Integer cantidad = 0;

    @Column(nullable = false)
    private Integer stockMinimo = 5;

    public StockSucursal() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public String getIdSucursal() { return idSucursal; }
    public void setIdSucursal(String idSucursal) { this.idSucursal = idSucursal; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
}
