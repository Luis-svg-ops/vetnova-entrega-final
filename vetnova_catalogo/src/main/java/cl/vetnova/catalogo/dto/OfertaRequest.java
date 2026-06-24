package cl.vetnova.catalogo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OfertaRequest(
        @NotNull(message = "El producto asociado es obligatorio")
        Long productoId,

        @NotNull(message = "El descuento es obligatorio")
        @Positive(message = "El descuento debe ser mayor a cero")
        Double descuento,

        LocalDate fechaInicio,

        LocalDate fechaFin,

        Boolean activa
) {}
