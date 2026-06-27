package cl.vetnova.agenda.dto;

import java.time.LocalDateTime;

import cl.vetnova.agenda.model.Cita;

public class CitaResponse {

    private Long id;
    private Long clienteId;
    private Long mascotaId;
    private Long veterinarioId;
    private Long servicioId;
    private Long boxId;
    private String sucursal;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private String estado;
    private String motivoCancelacion;
    private String canal;
    private LocalDateTime fechaCreacion;

    private String nombreCliente;
    private String nombreMascota;
    private String nombreVeterinario;

    public CitaResponse(Cita cita, String nombreCliente, String nombreMascota, String nombreVeterinario) {
        this.id = cita.getId();
        this.clienteId = cita.getClienteId();
        this.mascotaId = cita.getMascotaId();
        this.veterinarioId = cita.getVeterinarioId();
        this.servicioId = cita.getServicioId();
        this.boxId = cita.getBoxId();
        this.sucursal = cita.getSucursal();
        this.fechaHora = cita.getFechaHora();
        this.duracionMinutos = cita.getDuracionMinutos();
        this.estado = cita.getEstado();
        this.motivoCancelacion = cita.getMotivoCancelacion();
        this.canal = cita.getCanal();
        this.fechaCreacion = cita.getFechaCreacion();
        this.nombreCliente = nombreCliente;
        this.nombreMascota = nombreMascota;
        this.nombreVeterinario = nombreVeterinario;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public Long getMascotaId() { return mascotaId; }
    public Long getVeterinarioId() { return veterinarioId; }
    public Long getServicioId() { return servicioId; }
    public Long getBoxId() { return boxId; }
    public String getSucursal() { return sucursal; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public String getEstado() { return estado; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public String getCanal() { return canal; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getNombreCliente() { return nombreCliente; }
    public String getNombreMascota() { return nombreMascota; }
    public String getNombreVeterinario() { return nombreVeterinario; }
}
