package cl.vetnova.fichaclinica.dto;

import java.sql.Date;

import jakarta.validation.constraints.NotNull;

public record FichaClinicaRequest(
        @NotNull(message = "La mascota es obligatoria")
        Long mascotaId,

        Date fechaCreacion,

        String observacionesGenerales
) {}
