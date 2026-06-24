package cl.vetnova.auth.dto;

/**
 * Datos para registrar un acceso de auditoría (POST /api/auditoria).
 */
public record CrearAuditoriaRequest(
        Long usuarioId,
        String accion,
        String ip,
        Boolean exitoso,
        String detalles
) {}
