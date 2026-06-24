package cl.vetnova.inventario.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProductoResponse {

    private Long id;
    private String sku;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private List<StockSucursalResponse> stock;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<StockSucursalResponse> getStock() { return stock; }
    public void setStock(List<StockSucursalResponse> stock) { this.stock = stock; }
}
