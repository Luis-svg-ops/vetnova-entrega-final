package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ApiResponseTest {

    @Test
    void testOkConstruyeLaRespuestaCompleta() {
        ApiResponse<String> respuesta = ApiResponse.ok("operacion exitosa", "dato");

        assertTrue(respuesta.success());
        assertEquals("operacion exitosa", respuesta.message());
        assertEquals("dato", respuesta.data());
        assertNotNull(respuesta.timestamp());
    }
}
