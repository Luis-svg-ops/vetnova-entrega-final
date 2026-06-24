package cl.vetnova.inventario.exception;

/**
 * Conflicto con el estado actual (duplicado, unicidad). Se traduce a HTTP 409.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
