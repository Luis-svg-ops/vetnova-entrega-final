package cl.vetnova.auth.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "roles_permisos")
public class RolPermiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombreRol;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rol_permiso_items", joinColumns = @JoinColumn(name = "rol_id"))
    @Column(name = "permiso", nullable = false, length = 80)
    private Set<String> permisos = new LinkedHashSet<>();

    public RolPermiso() {}

    public RolPermiso(String nombreRol, String descripcion, Set<String> permisos) {
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        this.permisos = permisos == null ? new LinkedHashSet<>() : new LinkedHashSet<>(permisos);
        this.activo = true;
    }

    public void asignarPermiso(String permiso) {
        this.permisos.add(permiso);
    }

    public void revocarPermiso(String permiso) {
        this.permisos.remove(permiso);
    }

    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }

    public Long getId() { return id; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Set<String> getPermisos() { return permisos; }
    public void setPermisos(Set<String> permisos) { this.permisos = permisos; }
}
