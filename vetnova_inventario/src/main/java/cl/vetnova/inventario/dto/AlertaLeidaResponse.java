package cl.vetnova.inventario.dto;

import cl.vetnova.inventario.model.AlertaStock;

public class AlertaLeidaResponse {

    private AlertaStock alerta;
    private String mensaje;

    public AlertaLeidaResponse() {
    }

    public AlertaLeidaResponse(AlertaStock alerta, String mensaje) {
        this.alerta = alerta;
        this.mensaje = mensaje;
    }

    public AlertaStock getAlerta() { return alerta; }
    public void setAlerta(AlertaStock alerta) { this.alerta = alerta; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
