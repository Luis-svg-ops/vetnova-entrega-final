package cl.vetnova.notificaciones.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_alerta")
public class ConfiguracionAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "tipo_evento", length = 50)
    private String tipoEvento;

    @Column(name = "canal", length = 30)
    private String canal;

    @Column(name = "activa")
    private Boolean activa;

    public ConfiguracionAlerta() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}
