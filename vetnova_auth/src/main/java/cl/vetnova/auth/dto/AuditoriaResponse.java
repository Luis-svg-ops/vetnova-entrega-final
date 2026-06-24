package cl.vetnova.auth.dto;

import cl.vetnova.auth.model.AuditoriaAcceso;
import java.time.LocalDateTime;

public record AuditoriaResponse(
        Long id,
        Long usuarioId,
        String accion,
        String ip,
        LocalDateTime fechaHora,
        Boolean exitoso,
        String detalles
) {
    public static AuditoriaResponse from(AuditoriaAcceso a) {
        Long usuarioId = a.getUsuario() == null ? null : a.getUsuario().getId();
        return new AuditoriaResponse(a.getId(), usuarioId, a.getAccion(), a.getIp(),
                a.getTimestamp(), a.getExitoso(), a.getDetalle());
    }
}
