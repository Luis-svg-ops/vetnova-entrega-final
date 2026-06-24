package cl.vetnova.auth.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        boolean success,
        String message,
        String path,
        int status,
        Map<String, String> errors,
        LocalDateTime timestamp
) {}
