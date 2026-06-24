package cl.vetnova.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank String actual,
        @NotBlank @Size(min = 8, max = 80) String nueva
) {}
