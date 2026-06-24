package cl.vetnova.fichaclinica.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vacunas")

public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ficha_id")
    private Long fichaId;

    @Column(name = "veterinario_id")
    private Long veterinarioId;

    @Column(name = "nombre", length = 150)
    private String nombre;

    @Column(name = "lote", length = 100)
    private String lote;

    @Column(name = "fecha_aplicacion")
    private Date fechaAplicacion;

    @Column(name = "fecha_proxima_dosis")
    private Date fechaProximaDosis;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    public Vacuna() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFichaId() { return fichaId; }
    public void setFichaId(Long fichaId) { this.fichaId = fichaId; }

    public Long getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Long veterinarioId) { this.veterinarioId = veterinarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public Date getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(Date fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }

    public Date getFechaProximaDosis() { return fechaProximaDosis; }
    public void setFechaProximaDosis(Date fechaProximaDosis) { this.fechaProximaDosis = fechaProximaDosis; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
