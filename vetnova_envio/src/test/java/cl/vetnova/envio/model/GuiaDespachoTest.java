package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GuiaDespachoTest {

    @Test
    void testGuiaDespacho() {
        GuiaDespacho g = new GuiaDespacho();
        g.setId(1L);
        assertEquals(1L, g.getId());
        g.setDespachoId(2L);
        assertEquals(2L, g.getDespachoId());
        g.setFolio("GD-001");
        assertEquals("GD-001", g.getFolio());
        g.setOrigen("Chillán");
        assertEquals("Chillán", g.getOrigen());
        g.setDestino("Los Ángeles");
        assertEquals("Los Ángeles", g.getDestino());
        LocalDateTime ahora = LocalDateTime.now();
        g.setFechaEmision(ahora);
        assertEquals(ahora, g.getFechaEmision());
        g.setResponsable("Bodega");
        assertEquals("Bodega", g.getResponsable());
        g.setProductos(List.of("ALM-001", "ALM-002"));
        assertEquals(2, g.getProductos().size());
    }
}
