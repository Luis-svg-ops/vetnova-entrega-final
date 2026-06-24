package cl.vetnova.envio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroGuia;

    @Column(nullable = false)
    private Long ordenId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEnvio tipoEnvio;

    @Column(nullable = false)
    private Long idSucursalOrigen;

    @Column(length = 200)
    private String direccionEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estadoActual = EstadoEnvio.PREPARANDO;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialTracking> historial = new ArrayList<>();

    public Envio() {
    }

    public void addTracking(HistorialTracking tracking) {
        tracking.setEnvio(this);
        this.historial.add(tracking);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }
    public TipoEnvio getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(TipoEnvio tipoEnvio) { this.tipoEnvio = tipoEnvio; }
    public Long getIdSucursalOrigen() { return idSucursalOrigen; }
    public void setIdSucursalOrigen(Long idSucursalOrigen) { this.idSucursalOrigen = idSucursalOrigen; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public EstadoEnvio getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoEnvio estadoActual) { this.estadoActual = estadoActual; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<HistorialTracking> getHistorial() { return historial; }
    public void setHistorial(List<HistorialTracking> historial) { this.historial = historial; }
}
