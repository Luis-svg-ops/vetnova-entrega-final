package cl.vetnova.auth.dto;

import java.time.LocalDateTime;

public record AuthResponse(
        String token,
        LocalDateTime expiracion,
        UsuarioResponse usuario
) {}
