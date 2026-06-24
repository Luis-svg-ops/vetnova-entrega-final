package cl.vetnova.catalogo.exception;

/**
 * Conflicto con el estado actual (recurso duplicado, unicidad). Se traduce a HTTP 409.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
