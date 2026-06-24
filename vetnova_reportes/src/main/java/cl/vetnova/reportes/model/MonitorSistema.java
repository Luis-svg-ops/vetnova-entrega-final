package cl.vetnova.reportes.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monitor_sistema")
public class MonitorSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "microservicio", length = 100)
    private String microservicio;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "latencia_ms")
    private Integer latenciaMs;

    @Column(name = "uso_cpu")
    private Double usoCpu;

    @Column(name = "uso_memoria")
    private Double usoMemoria;

    @Column(name = "ultimo_chequeo")
    private LocalDateTime ultimoChequeo;

    public MonitorSistema() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMicroservicio() { return microservicio; }
    public void setMicroservicio(String microservicio) { this.microservicio = microservicio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getLatenciaMs() { return latenciaMs; }
    public void setLatenciaMs(Integer latenciaMs) { this.latenciaMs = latenciaMs; }

    public Double getUsoCpu() { return usoCpu; }
    public void setUsoCpu(Double usoCpu) { this.usoCpu = usoCpu; }

    public Double getUsoMemoria() { return usoMemoria; }
    public void setUsoMemoria(Double usoMemoria) { this.usoMemoria = usoMemoria; }

    public LocalDateTime getUltimoChequeo() { return ultimoChequeo; }
    public void setUltimoChequeo(LocalDateTime ultimoChequeo) { this.ultimoChequeo = ultimoChequeo; }
}
