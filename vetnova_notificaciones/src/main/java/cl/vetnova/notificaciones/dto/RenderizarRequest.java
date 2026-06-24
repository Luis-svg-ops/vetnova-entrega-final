package cl.vetnova.notificaciones.dto;

import java.util.Map;

public class RenderizarRequest {
    private Map<String, String> valores;

    public Map<String, String> getValores() { return valores; }
    public void setValores(Map<String, String> valores) { this.valores = valores; }
}
