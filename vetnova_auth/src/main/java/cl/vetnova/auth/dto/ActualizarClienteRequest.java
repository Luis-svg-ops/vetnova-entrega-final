package cl.vetnova.auth.dto;

/**
 * Datos editables de un Cliente (PUT /api/v1/clientes/{id}): teléfono y dirección.
 */
public record ActualizarClienteRequest(
        String telefono,
        String direccion
) {}
