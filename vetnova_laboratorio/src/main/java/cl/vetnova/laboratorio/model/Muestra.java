package cl.vetnova.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "muestras")
public class Muestra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "orden_examen_id")
    private Long ordenExamenId;
    @Column(name = "tipo", length = 80)
    private String tipo;
    @Column(name = "codigo_muestra", unique = true, length = 80)
    private String codigoMuestra;
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;
    @Column(name = "estado_procesamiento", length = 40)
    private String estadoProcesamiento;
    @Column(name = "responsable_recepcion")
    private Long responsableRecepcion;

    public Muestra() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrdenExamenId() { return ordenExamenId; }
    public void setOrdenExamenId(Long ordenExamenId) { this.ordenExamenId = ordenExamenId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCodigoMuestra() { return codigoMuestra; }
    public void setCodigoMuestra(String codigoMuestra) { this.codigoMuestra = codigoMuestra; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }
    public String getEstadoProcesamiento() { return estadoProcesamiento; }
    public void setEstadoProcesamiento(String estadoProcesamiento) { this.estadoProcesamiento = estadoProcesamiento; }
    public Long getResponsableRecepcion() { return responsableRecepcion; }
    public void setResponsableRecepcion(Long responsableRecepcion) { this.responsableRecepcion = responsableRecepcion; }
}
