package cl.vetnova.auth.dto;

/**
 * Datos de entrada para crear un Usuario (POST /api/usuarios).
 * Las validaciones de negocio (formato, política de password, rol y unicidad)
 * se aplican en el servicio para devolver mensajes y códigos HTTP precisos.
 */
public record CrearUsuarioRequest(
        String nombre,
        String email,
        String telefono,
        String password,
        String nombreRol
) {}
