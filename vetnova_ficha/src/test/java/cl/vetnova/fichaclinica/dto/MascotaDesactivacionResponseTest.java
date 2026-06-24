package cl.vetnova.fichaclinica.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import cl.vetnova.fichaclinica.model.Mascota;

public class MascotaDesactivacionResponseTest {

    @Test
    void testConstructorYSetters() {
        Mascota m = new Mascota();
        m.setId(1L);
        MascotaDesactivacionResponse resp = new MascotaDesactivacionResponse(m, "ok");
        assertEquals(1L, resp.getMascota().getId());
        assertEquals("ok", resp.getMensaje());

        MascotaDesactivacionResponse vacio = new MascotaDesactivacionResponse();
        vacio.setMascota(m);
        vacio.setMensaje("otro");
        assertEquals(m, vacio.getMascota());
        assertEquals("otro", vacio.getMensaje());
    }
}
