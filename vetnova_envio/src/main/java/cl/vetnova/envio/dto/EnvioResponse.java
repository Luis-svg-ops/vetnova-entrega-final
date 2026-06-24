package cl.vetnova.envio.dto;

import java.time.LocalDateTime;
import java.util.List;

public class EnvioResponse {

    private Long id;
    private String numeroGuia;
    private Long ordenId;
    private String tipoEnvio;
    private Long idSucursalOrigen;
    private String direccionEntrega;
    private String estadoActual;
    private LocalDateTime fechaCreacion;
    private List<TrackingResponse> historial;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }
    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }
    public Long getIdSucursalOrigen() { return idSucursalOrigen; }
    public void setIdSucursalOrigen(Long idSucursalOrigen) { this.idSucursalOrigen = idSucursalOrigen; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<TrackingResponse> getHistorial() { return historial; }
    public void setHistorial(List<TrackingResponse> historial) { this.historial = historial; }
}
