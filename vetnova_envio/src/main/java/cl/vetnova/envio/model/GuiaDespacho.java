package cl.vetnova.envio.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "guias_despacho")
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "despacho_id")
    private Long despachoId;

    @Column(name = "folio", length = 50)
    private String folio;

    @Column(name = "origen", length = 150)
    private String origen;

    @Column(name = "destino", length = 150)
    private String destino;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Column(name = "responsable", length = 120)
    private String responsable;

    @Transient
    private List<String> productos = new ArrayList<>();

    public GuiaDespacho() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDespachoId() { return despachoId; }
    public void setDespachoId(Long despachoId) { this.despachoId = despachoId; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public List<String> getProductos() { return productos; }
    public void setProductos(List<String> productos) { this.productos = productos; }
}
