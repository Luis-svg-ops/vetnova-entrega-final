package cl.vetnova.auth.exception;

/**
 * Se lanza al intentar modificar o eliminar un recurso inmutable
 * (p. ej. los registros de auditoría). Se traduce a HTTP 405.
 */
public class RegistroInmutableException extends RuntimeException {
    public RegistroInmutableException(String message) {
        super(message);
    }
}
