package cl.vetnova.agenda.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recordatorios")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cita_id")
    private Long citaId;

    @Column(name = "tipo", length = 20)
    private String tipo;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "mensaje", length = 500)
    private String mensaje;

    @Column(name = "enviado")
    private Boolean enviado;

    @Column(name = "cancelado")
    private Boolean cancelado;

    public Recordatorio() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getEnviado() { return enviado; }
    public void setEnviado(Boolean enviado) { this.enviado = enviado; }

    public Boolean getCancelado() { return cancelado; }
    public void setCancelado(Boolean cancelado) { this.cancelado = cancelado; }
}
