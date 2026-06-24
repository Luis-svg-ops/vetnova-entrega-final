package cl.vetnova.catalogo.exception;

/**
 * Regla de negocio incumplida. Se traduce a HTTP 400.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
