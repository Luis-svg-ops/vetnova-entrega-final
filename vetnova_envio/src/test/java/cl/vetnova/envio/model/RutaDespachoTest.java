package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RutaDespachoTest {

    @Test
    void testRutaDespacho() {
        RutaDespacho r = new RutaDespacho();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setSucursalOrigen("Chillán");
        assertEquals("Chillán", r.getSucursalOrigen());
        r.setSucursalDestino("Talca");
        assertEquals("Talca", r.getSucursalDestino());
        r.setDistanciaKm(180.5);
        assertEquals(180.5, r.getDistanciaKm());
        r.setTiempoEstimadoMin(150);
        assertEquals(150, r.getTiempoEstimadoMin());
        r.setActiva(true);
        assertEquals(true, r.getActiva());
    }
}
