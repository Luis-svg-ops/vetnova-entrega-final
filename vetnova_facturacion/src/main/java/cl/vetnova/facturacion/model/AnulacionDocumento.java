package cl.vetnova.facturacion.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "anulaciones_documento")
public class AnulacionDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_id")
    private Long documentoId;

    @Column(name = "administrador_id")
    private Long administradorId;

    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "estado_sii", length = 50)
    private String estadoSII;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    public AnulacionDocumento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDocumentoId() { return documentoId; }
    public void setDocumentoId(Long documentoId) { this.documentoId = documentoId; }

    public Long getAdministradorId() { return administradorId; }
    public void setAdministradorId(Long administradorId) { this.administradorId = administradorId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstadoSII() { return estadoSII; }
    public void setEstadoSII(String estadoSII) { this.estadoSII = estadoSII; }

    public LocalDateTime getFechaAnulacion() { return fechaAnulacion; }
    public void setFechaAnulacion(LocalDateTime fechaAnulacion) { this.fechaAnulacion = fechaAnulacion; }
}
