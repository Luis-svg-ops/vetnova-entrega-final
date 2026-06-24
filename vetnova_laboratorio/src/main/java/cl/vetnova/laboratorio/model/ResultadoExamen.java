package cl.vetnova.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultados_examen")
public class ResultadoExamen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "orden_examen_id")
    private Long ordenExamenId;
    @Column(name = "muestra_id")
    private Long muestraId;
    @Column(name = "tecnico_id")
    private Long tecnicoId;
    @Column(name = "resultado", length = 2000)
    private String resultado;
    @Column(name = "observaciones", length = 1000)
    private String observaciones;
    @Column(name = "disponible")
    private Boolean disponible;
    @Column(name = "ficha_id")
    private Long fichaId;
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public ResultadoExamen() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrdenExamenId() { return ordenExamenId; }
    public void setOrdenExamenId(Long ordenExamenId) { this.ordenExamenId = ordenExamenId; }
    public Long getMuestraId() { return muestraId; }
    public void setMuestraId(Long muestraId) { this.muestraId = muestraId; }
    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
    public Long getFichaId() { return fichaId; }
    public void setFichaId(Long fichaId) { this.fichaId = fichaId; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
