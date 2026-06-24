package cl.vetnova.agenda.exception;

import static org.junit.jupiter.api.Assertions.*;

import cl.vetnova.agenda.dto.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/prueba");

    void metodoDeApoyo(String valor) {
    }

    private MethodArgumentNotValidException validacionFallida() {
        try {
            BeanPropertyBindingResult resultado = new BeanPropertyBindingResult(new Object(), "request");
            resultado.addError(new FieldError("request", "campo", "no debe ser nulo"));
            MethodParameter parametro = new MethodParameter(
                    getClass().getDeclaredMethod("metodoDeApoyo", String.class), 0);
            return new MethodArgumentNotValidException(parametro, resultado);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void testNotFoundResponde404() {
        ResponseEntity<ErrorResponse> respuesta = handler.notFound(new ResourceNotFoundException("recurso no encontrado"), request);

        assertEquals(404, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().isSuccess());
        assertNotNull(respuesta.getBody().getMessage());
    }

    @Test
    void testValidationResponde400() {
        ResponseEntity<ErrorResponse> respuesta = handler.validation(validacionFallida(), request);

        assertEquals(400, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().isSuccess());
        assertNotNull(respuesta.getBody().getMessage());
    }

    @Test
    void testBusinessResponde400() {
        ResponseEntity<ErrorResponse> respuesta = handler.business(new BusinessRuleException("regla"), request);
        assertEquals(400, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().isSuccess());
        assertNotNull(respuesta.getBody().getMessage());
    }

    @Test
    void testConflictResponde409() {
        ResponseEntity<ErrorResponse> respuesta = handler.conflict(new ConflictException("conflicto"), request);
        assertEquals(409, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().isSuccess());
        assertNotNull(respuesta.getBody().getMessage());
    }

    @Test
    void testInmutableResponde405() {
        ResponseEntity<ErrorResponse> respuesta = handler.inmutable(new RegistroInmutableException("inmutable"), request);
        assertEquals(405, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().isSuccess());
        assertNotNull(respuesta.getBody().getMessage());
    }

    @Test
    void testGenericResponde500() {
        ResponseEntity<ErrorResponse> respuesta = handler.generic(new RuntimeException("error inesperado"), request);

        assertEquals(500, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().isSuccess());
        assertNotNull(respuesta.getBody().getMessage());
    }
}
