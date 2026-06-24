package cl.vetnova.auth.dto;

/**
 * Cuerpo para asignar un permiso a un rol (PUT /api/roles/{id}/permisos).
 */
public record AsignarPermisoRequest(
        String permiso
) {}
