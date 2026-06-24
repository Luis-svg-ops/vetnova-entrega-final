package cl.vetnova.soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "escalamientos_ticket")
public class EscalamientoTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticket_id")
    private Long ticketId;
    @Column(name = "administrador_id")
    private Long administradorId;
    @Column(name = "motivo", length = 400)
    private String motivo;
    @Column(name = "estado", length = 30)
    private String estado;
    @Column(name = "ultima_accion", length = 400)
    private String ultimaAccion;
    @Column(name = "fecha_gestion")
    private LocalDateTime fechaGestion;
    @Column(name = "resolucion", length = 800)
    private String resolucion;
    @Column(name = "fecha_escalamiento")
    private LocalDateTime fechaEscalamiento;
    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public EscalamientoTicket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getAdministradorId() { return administradorId; }
    public void setAdministradorId(Long administradorId) { this.administradorId = administradorId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUltimaAccion() { return ultimaAccion; }
    public void setUltimaAccion(String ultimaAccion) { this.ultimaAccion = ultimaAccion; }
    public LocalDateTime getFechaGestion() { return fechaGestion; }
    public void setFechaGestion(LocalDateTime fechaGestion) { this.fechaGestion = fechaGestion; }
    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    public LocalDateTime getFechaEscalamiento() { return fechaEscalamiento; }
    public void setFechaEscalamiento(LocalDateTime fechaEscalamiento) { this.fechaEscalamiento = fechaEscalamiento; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}
