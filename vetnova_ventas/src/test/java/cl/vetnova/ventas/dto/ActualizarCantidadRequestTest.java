package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ActualizarCantidadRequestTest {

    @Test
    void testActualizarCantidadRequest() {
        ActualizarCantidadRequest request = new ActualizarCantidadRequest();
        request.setCantidad(4);
        assertEquals(4, request.getCantidad());
    }
}
