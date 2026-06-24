package cl.vetnova.agenda.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "disponibilidad_profesional")

public class DisponibilidadProfesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "veterinario_id")
    private Long veterinarioId;

    @Column(name = "dia_semana", length = 20)
    private String diaSemana;

    @Column(name = "hora_inicio", length = 10)
    private String horaInicio;

    @Column(name = "hora_fin", length = 10)
    private String horaFin;

    @Column(name = "sucursal", length = 150)
    private String sucursal;

    @Column(name = "activa")
    private Boolean activa;

    public DisponibilidadProfesional() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Long veterinarioId) { this.veterinarioId = veterinarioId; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}
