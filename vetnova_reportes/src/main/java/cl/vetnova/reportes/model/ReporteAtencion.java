package cl.vetnova.reportes.model;

import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "reportes_atencion")
public class ReporteAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporte_id")
    private Long reporteId;

    @Column(name = "total_citas")
    private Integer totalCitas;

    @Column(name = "citas_realizadas")
    private Integer citasRealizadas;

    @Column(name = "citas_canceladas")
    private Integer citasCanceladas;

    @Column(name = "citas_ausentes")
    private Integer citasAusentes;

    @ElementCollection
    @CollectionTable(name = "atencion_por_veterinario", joinColumns = @JoinColumn(name = "reporte_atencion_id"))
    @MapKeyColumn(name = "veterinario")
    @Column(name = "cantidad")
    private Map<String, Integer> atencionPorVeterinario;

    @ElementCollection
    @CollectionTable(name = "atencion_por_servicio", joinColumns = @JoinColumn(name = "reporte_atencion_id"))
    @MapKeyColumn(name = "servicio")
    @Column(name = "cantidad")
    private Map<String, Integer> atencionPorServicio;

    public ReporteAtencion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReporteId() { return reporteId; }
    public void setReporteId(Long reporteId) { this.reporteId = reporteId; }

    public Integer getTotalCitas() { return totalCitas; }
    public void setTotalCitas(Integer totalCitas) { this.totalCitas = totalCitas; }

    public Integer getCitasRealizadas() { return citasRealizadas; }
    public void setCitasRealizadas(Integer citasRealizadas) { this.citasRealizadas = citasRealizadas; }

    public Integer getCitasCanceladas() { return citasCanceladas; }
    public void setCitasCanceladas(Integer citasCanceladas) { this.citasCanceladas = citasCanceladas; }

    public Integer getCitasAusentes() { return citasAusentes; }
    public void setCitasAusentes(Integer citasAusentes) { this.citasAusentes = citasAusentes; }

    public Map<String, Integer> getAtencionPorVeterinario() { return atencionPorVeterinario; }
    public void setAtencionPorVeterinario(Map<String, Integer> atencionPorVeterinario) { this.atencionPorVeterinario = atencionPorVeterinario; }

    public Map<String, Integer> getAtencionPorServicio() { return atencionPorServicio; }
    public void setAtencionPorServicio(Map<String, Integer> atencionPorServicio) { this.atencionPorServicio = atencionPorServicio; }
}
