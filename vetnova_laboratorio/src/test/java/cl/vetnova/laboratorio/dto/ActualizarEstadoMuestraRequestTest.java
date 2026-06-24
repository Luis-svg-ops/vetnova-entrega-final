package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ActualizarEstadoMuestraRequestTest {

    @Test
    void testGettersYSetters() {
        ActualizarEstadoMuestraRequest r = new ActualizarEstadoMuestraRequest();
        r.setEstado("EN_PROCESO");
        assertEquals("EN_PROCESO", r.getEstado());
    }
}
