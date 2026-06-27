package cl.vetnova.fichaclinica.dto;

import java.time.LocalDate;

import cl.vetnova.fichaclinica.model.Mascota;

public class MascotaResponse {

    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private Double peso;
    private String microchip;
    private Boolean activo;

    public MascotaResponse(Mascota m, String nombreCliente) {
        this.id = m.getId();
        this.clienteId = m.getClienteId();
        this.nombreCliente = nombreCliente;
        this.nombre = m.getNombre();
        this.especie = m.getEspecie();
        this.raza = m.getRaza();
        this.sexo = m.getSexo();
        this.fechaNacimiento = m.getFechaNacimiento();
        this.peso = m.getPeso();
        this.microchip = m.getMicrochip();
        this.activo = m.getActivo();
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public String getNombreCliente() { return nombreCliente; }
    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public String getSexo() { return sexo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public Double getPeso() { return peso; }
    public String getMicrochip() { return microchip; }
    public Boolean getActivo() { return activo; }
}
