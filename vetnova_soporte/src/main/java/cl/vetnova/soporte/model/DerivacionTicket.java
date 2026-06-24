package cl.vetnova.soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "derivaciones_ticket")
public class DerivacionTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticket_id")
    private Long ticketId;
    @Column(name = "responsable_anterior")
    private Long responsableAnterior;
    @Column(name = "responsable_nuevo")
    private Long responsableNuevo;
    @Column(name = "motivo", length = 300)
    private String motivo;
    @Column(name = "fecha")
    private LocalDateTime fecha;

    public DerivacionTicket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getResponsableAnterior() { return responsableAnterior; }
    public void setResponsableAnterior(Long responsableAnterior) { this.responsableAnterior = responsableAnterior; }
    public Long getResponsableNuevo() { return responsableNuevo; }
    public void setResponsableNuevo(Long responsableNuevo) { this.responsableNuevo = responsableNuevo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
