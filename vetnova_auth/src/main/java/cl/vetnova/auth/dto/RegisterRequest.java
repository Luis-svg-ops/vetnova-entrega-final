package cl.vetnova.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 120) String nombre,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 8, max = 30) String telefono,
        @NotBlank @Size(min = 8, max = 80) String password,
        @NotBlank @Pattern(regexp = "ADMIN_SISTEMA|ADMIN_SUCURSAL|CLIENTE|RECEPCIONISTA|VETERINARIO|BODEGUERO") String rol
) {}
