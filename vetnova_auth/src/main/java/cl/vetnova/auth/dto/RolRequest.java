package cl.vetnova.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record RolRequest(
        @NotBlank @Size(max = 60) String nombreRol,
        @NotBlank @Size(max = 200) String descripcion,
        @NotEmpty Set<@NotBlank String> permisos
) {}
