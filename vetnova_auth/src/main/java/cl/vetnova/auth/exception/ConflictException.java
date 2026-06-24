package cl.vetnova.auth.exception;

/**
 * Se lanza cuando una operación choca con el estado actual del sistema
 * (recurso duplicado, unicidad violada, etc.). Se traduce a HTTP 409.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
