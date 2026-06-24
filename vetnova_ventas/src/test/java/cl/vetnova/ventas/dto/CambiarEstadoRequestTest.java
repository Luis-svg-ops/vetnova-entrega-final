package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class CambiarEstadoRequestTest {

    @Test
    void testCambiarEstadoRequest() {
        CambiarEstadoRequest cambiarEstadoRequest = new CambiarEstadoRequest();
        cambiarEstadoRequest.setEstado("x");
        assertEquals("x", cambiarEstadoRequest.getEstado());
    }

}