package cl.vetnova.facturacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "folios")
public class Folio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sucursal")
    private Long sucursal;

    @Column(name = "tipo_documento", length = 50)
    private String tipoDocumento;

    @Column(name = "folio_desde")
    private Integer folioDesde;

    @Column(name = "folio_hasta")
    private Integer folioHasta;

    @Column(name = "folio_actual")
    private Integer folioActual;

    @Column(name = "folios_restantes")
    private Integer foliosRestantes;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "umbral")
    private Integer umbral;

    public Folio() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSucursal() { return sucursal; }
    public void setSucursal(Long sucursal) { this.sucursal = sucursal; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Integer getFolioDesde() { return folioDesde; }
    public void setFolioDesde(Integer folioDesde) { this.folioDesde = folioDesde; }

    public Integer getFolioHasta() { return folioHasta; }
    public void setFolioHasta(Integer folioHasta) { this.folioHasta = folioHasta; }

    public Integer getFolioActual() { return folioActual; }
    public void setFolioActual(Integer folioActual) { this.folioActual = folioActual; }

    public Integer getFoliosRestantes() { return foliosRestantes; }
    public void setFoliosRestantes(Integer foliosRestantes) { this.foliosRestantes = foliosRestantes; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Integer getUmbral() { return umbral; }
    public void setUmbral(Integer umbral) { this.umbral = umbral; }
}
