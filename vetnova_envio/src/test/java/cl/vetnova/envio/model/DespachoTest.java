package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DespachoTest {

    @Test
    void testDespacho() {
        Despacho d = new Despacho();
        d.setId(1L);
        assertEquals(1L, d.getId());
        d.setOrdenId(2L);
        assertEquals(2L, d.getOrdenId());
        d.setSucursalOrigen("Chillán");
        assertEquals("Chillán", d.getSucursalOrigen());
        d.setSucursalDestino("Talca");
        assertEquals("Talca", d.getSucursalDestino());
        d.setTipo("INTERSUCURSAL");
        assertEquals("INTERSUCURSAL", d.getTipo());
        d.setEstado("EN_RUTA");
        assertEquals("EN_RUTA", d.getEstado());
        d.setResponsable("Bodega");
        assertEquals("Bodega", d.getResponsable());
        LocalDateTime ahora = LocalDateTime.now();
        d.setFechaCreacion(ahora);
        assertEquals(ahora, d.getFechaCreacion());
        d.setFechaEstimada(ahora);
        assertEquals(ahora, d.getFechaEstimada());
        d.setFechaEntrega(ahora);
        assertEquals(ahora, d.getFechaEntrega());
    }
}
