package cl.vetnova.auth.dto;

import java.util.Set;

public record ValidateTokenResponse(
        boolean valido,
        Long usuarioId,
        String rol,
        Set<String> permisos
) {}
