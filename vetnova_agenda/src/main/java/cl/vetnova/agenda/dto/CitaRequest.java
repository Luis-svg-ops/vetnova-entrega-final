package cl.vetnova.agenda.dto;

import java.time.LocalDateTime;

public record CitaRequest(
        Long clienteId,
        Long mascotaId,
        Long veterinarioId,
        Long servicioId,
        Long boxId,
        String sucursal,
        LocalDateTime fechaHora,
        Integer duracionMinutos,
        String canal
) {}
