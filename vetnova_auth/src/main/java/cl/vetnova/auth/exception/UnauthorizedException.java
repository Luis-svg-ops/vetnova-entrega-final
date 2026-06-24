package cl.vetnova.auth.exception;

/**
 * Se lanza ante fallos de autenticación (credenciales inválidas, token
 * inválido/expirado/revocado). Se traduce a HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
