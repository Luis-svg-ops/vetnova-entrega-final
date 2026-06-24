package cl.vetnova.fichaclinica.model;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "procedimientos")

public class Procedimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ficha_id")
    private Long fichaId;

    @Column(name = "veterinario_id")
    private Long veterinarioId;

    @Column(name = "tipo", length = 100)
    private String tipo;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "resultado", length = 2000)
    private String resultado;

    @Column(name = "nombre", length = 150)
    private String nombre;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public Procedimiento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFichaId() { return fichaId; }
    public void setFichaId(Long fichaId) { this.fichaId = fichaId; }

    public Long getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Long veterinarioId) { this.veterinarioId = veterinarioId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
