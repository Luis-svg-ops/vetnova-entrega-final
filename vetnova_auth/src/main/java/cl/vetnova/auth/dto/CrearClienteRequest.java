package cl.vetnova.auth.dto;

/**
 * Datos para registrar un Cliente (POST /api/v1/clientes).
 * Las validaciones (RUT, coherencia de email con el Usuario, unicidad)
 * se aplican en el servicio para devolver mensajes y códigos HTTP precisos.
 */
public record CrearClienteRequest(
        Long usuarioId,
        String rut,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String direccion
) {}
