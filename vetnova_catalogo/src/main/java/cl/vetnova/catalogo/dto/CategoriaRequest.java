package cl.vetnova.catalogo.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String nombre,

        String descripcion,

        @NotBlank(message = "El tipo es obligatorio (PRODUCTO o SERVICIO)")
        String tipo
) {}
