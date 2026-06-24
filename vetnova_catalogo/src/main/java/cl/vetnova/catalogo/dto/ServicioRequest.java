package cl.vetnova.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ServicioRequest(
        @NotBlank(message = "El nombre del servicio es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        Double precio,

        @Positive(message = "La duración debe ser mayor a cero")
        Integer duracionMinutos,

        Boolean activo,

        Long categoriaId
) {}
