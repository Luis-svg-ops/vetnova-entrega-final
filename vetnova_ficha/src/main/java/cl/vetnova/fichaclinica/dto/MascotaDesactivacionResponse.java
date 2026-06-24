package cl.vetnova.fichaclinica.dto;

import cl.vetnova.fichaclinica.model.Mascota;

public class MascotaDesactivacionResponse {

    private Mascota mascota;
    private String mensaje;

    public MascotaDesactivacionResponse() {
    }

    public MascotaDesactivacionResponse(Mascota mascota, String mensaje) {
        this.mascota = mascota;
        this.mensaje = mensaje;
    }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
