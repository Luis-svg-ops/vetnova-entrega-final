package cl.vetnova.agenda.dto;

import java.time.LocalDateTime;

public record ReprogramarCitaRequest(
        LocalDateTime fechaHora,
        Integer duracionMinutos
) {}
