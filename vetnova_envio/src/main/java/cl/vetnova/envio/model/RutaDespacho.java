package cl.vetnova.envio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rutas_despacho")
public class RutaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sucursal_origen", length = 150)
    private String sucursalOrigen;

    @Column(name = "sucursal_destino", length = 150)
    private String sucursalDestino;

    @Column(name = "distancia_km")
    private Double distanciaKm;

    @Column(name = "tiempo_estimado_min")
    private Integer tiempoEstimadoMin;

    @Column(name = "activa")
    private Boolean activa = true;

    public RutaDespacho() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSucursalOrigen() { return sucursalOrigen; }
    public void setSucursalOrigen(String sucursalOrigen) { this.sucursalOrigen = sucursalOrigen; }

    public String getSucursalDestino() { return sucursalDestino; }
    public void setSucursalDestino(String sucursalDestino) { this.sucursalDestino = sucursalDestino; }

    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }

    public Integer getTiempoEstimadoMin() { return tiempoEstimadoMin; }
    public void setTiempoEstimadoMin(Integer tiempoEstimadoMin) { this.tiempoEstimadoMin = tiempoEstimadoMin; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}
