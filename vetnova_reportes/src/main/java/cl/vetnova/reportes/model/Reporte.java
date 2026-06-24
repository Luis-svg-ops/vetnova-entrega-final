package cl.vetnova.reportes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", length = 30)
    private String tipo; // ATENCION, VENTA, STOCK

    @Column(name = "sucursal")
    private Long sucursal;

    @Column(name = "desde")
    private LocalDate desde;

    @Column(name = "hasta")
    private LocalDate hasta;

    @Column(name = "generado_por")
    private Long generadoPor;

    @Column(name = "generado_en")
    private LocalDateTime generadoEn;

    @Column(name = "estado", length = 30)
    private String estado; // GENERADO, VACIO

    public Reporte() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getSucursal() { return sucursal; }
    public void setSucursal(Long sucursal) { this.sucursal = sucursal; }

    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }

    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }

    public Long getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(Long generadoPor) { this.generadoPor = generadoPor; }

    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(LocalDateTime generadoEn) { this.generadoEn = generadoEn; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
