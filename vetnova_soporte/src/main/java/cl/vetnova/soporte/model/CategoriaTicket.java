package cl.vetnova.soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias_ticket")
public class CategoriaTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", unique = true, length = 80)
    private String nombre;
    @Column(name = "descripcion", length = 250)
    private String descripcion;
    @Column(name = "area_por_defecto", length = 60)
    private String areaPorDefecto;
    @Column(name = "prioridad_default", length = 20)
    private String prioridadDefault;

    public CategoriaTicket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getAreaPorDefecto() { return areaPorDefecto; }
    public void setAreaPorDefecto(String areaPorDefecto) { this.areaPorDefecto = areaPorDefecto; }
    public String getPrioridadDefault() { return prioridadDefault; }
    public void setPrioridadDefault(String prioridadDefault) { this.prioridadDefault = prioridadDefault; }
}
