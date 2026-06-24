package cl.vetnova.notificaciones.model;

import jakarta.persistence.*;

@Entity
@Table(name = "canal_notificacion")
public class CanalNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "tipo", length = 30)
    private String tipo;

    @Column(name = "destino", length = 200)
    private String destino;

    @Column(name = "activo")
    private Boolean activo;

    public CanalNotificacion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
