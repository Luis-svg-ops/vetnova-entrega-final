package cl.vetnova.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePerfilRequest(
        @NotBlank @Size(min = 3, max = 120) String nombre,
        @NotBlank @Size(min = 8, max = 30) String telefono
) {}
