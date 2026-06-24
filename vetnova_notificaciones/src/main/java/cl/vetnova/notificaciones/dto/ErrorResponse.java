package cl.vetnova.notificaciones.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private boolean success;
    private String message;
    private String path;
    private int status;
    private Map<String, String> errors;
    private LocalDateTime timestamp;

    public ErrorResponse(boolean success, String message, String path, int status,
                         Map<String, String> errors, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.path = path;
        this.status = status;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public int getStatus() { return status; }
    public Map<String, String> getErrors() { return errors; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
