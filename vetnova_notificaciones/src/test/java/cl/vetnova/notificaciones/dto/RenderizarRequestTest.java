package cl.vetnova.notificaciones.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class RenderizarRequestTest {

    @Test
    void testGettersYSetters() {
        RenderizarRequest r = new RenderizarRequest();
        Map<String, String> valores = Map.of("nombre", "Juan");
        r.setValores(valores);
        assertEquals(valores, r.getValores());
    }
}
