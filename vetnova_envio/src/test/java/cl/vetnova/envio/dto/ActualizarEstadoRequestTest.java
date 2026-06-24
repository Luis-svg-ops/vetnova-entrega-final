package cl.vetnova.envio.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class ActualizarEstadoRequestTest {

    @Test
    void testActualizarEstadoRequest() {
        ActualizarEstadoRequest actualizarEstadoRequest = new ActualizarEstadoRequest();
        actualizarEstadoRequest.setEstado("x");
        assertEquals("x", actualizarEstadoRequest.getEstado());
        actualizarEstadoRequest.setObservacion("x");
        assertEquals("x", actualizarEstadoRequest.getObservacion());
    }

}