package cl.vetnova.auth.dto;

/**
 * Datos para cambiar el password de un Usuario (PUT /api/usuarios/{id}/password).
 */
public record CambiarPasswordRequest(
        String passwordActual,
        String passwordNuevo
) {}
