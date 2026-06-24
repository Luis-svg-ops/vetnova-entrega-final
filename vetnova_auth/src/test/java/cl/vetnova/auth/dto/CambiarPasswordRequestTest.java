package cl.vetnova.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CambiarPasswordRequestTest {

    @Test
    void testCambiarPasswordRequestExponeSusComponentes() {
        CambiarPasswordRequest request = new CambiarPasswordRequest("Pass1234!", "NuevaClave9!");

        assertEquals("Pass1234!", request.passwordActual());
        assertEquals("NuevaClave9!", request.passwordNuevo());
    }
}
