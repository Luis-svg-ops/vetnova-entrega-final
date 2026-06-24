package cl.vetnova.envio.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entrada del historial cronológico de un {@link SeguimientoPedido}.
 */
@Entity
@Table(name = "registros_seguimiento")
public class RegistroSeguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seguimiento_id")
    private Long seguimientoId;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    public RegistroSeguimiento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSeguimientoId() { return seguimientoId; }
    public void setSeguimientoId(Long seguimientoId) { this.seguimientoId = seguimientoId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
