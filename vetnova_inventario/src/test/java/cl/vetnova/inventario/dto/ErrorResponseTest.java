package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class ErrorResponseTest {

    @Test
    void testErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse(false, "Recurso no encontrado", "/api/v1/inventario", 404,
                new HashMap<>(), LocalDateTime.now());
        assertFalse(errorResponse.isSuccess());
        assertEquals("Recurso no encontrado", errorResponse.getMessage());
        assertEquals("/api/v1/inventario", errorResponse.getPath());
        assertEquals(404, errorResponse.getStatus());
        assertNotNull(errorResponse.getErrors());
        assertNotNull(errorResponse.getTimestamp());
    }

}