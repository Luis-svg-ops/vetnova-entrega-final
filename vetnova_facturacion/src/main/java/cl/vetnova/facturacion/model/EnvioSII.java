package cl.vetnova.facturacion.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "envios_sii")
public class EnvioSII {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_id")
    private Long documentoId;

    @Column(name = "respuesta_codigo", length = 20)
    private String respuestaCodigo;

    @Column(name = "respuesta_descripcion", length = 500)
    private String respuestaDescripcion;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "reintentado")
    private Boolean reintentado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    public EnvioSII() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDocumentoId() { return documentoId; }
    public void setDocumentoId(Long documentoId) { this.documentoId = documentoId; }

    public String getRespuestaCodigo() { return respuestaCodigo; }
    public void setRespuestaCodigo(String respuestaCodigo) { this.respuestaCodigo = respuestaCodigo; }

    public String getRespuestaDescripcion() { return respuestaDescripcion; }
    public void setRespuestaDescripcion(String respuestaDescripcion) { this.respuestaDescripcion = respuestaDescripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Boolean getReintentado() { return reintentado; }
    public void setReintentado(Boolean reintentado) { this.reintentado = reintentado; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}
