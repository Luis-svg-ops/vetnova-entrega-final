package cl.vetnova.fichaclinica.dto;

import java.sql.Date;

public record FichaClinicaResponse(
        Long id,
        Long mascotaId,
        String nombreMascota,
        String especieMascota,
        Long clienteId,
        Date fechaCreacion,
        String observacionesGenerales
) {}
