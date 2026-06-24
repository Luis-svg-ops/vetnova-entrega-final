package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class RegistroSeguimientoTest {

    @Test
    void testRegistroSeguimiento() {
        LocalDateTime t = LocalDateTime.now();
        RegistroSeguimiento r = new RegistroSeguimiento();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setSeguimientoId(2L);
        assertEquals(2L, r.getSeguimientoId());
        r.setEstado("PREPARANDO");
        assertEquals("PREPARANDO", r.getEstado());
        r.setDescripcion("En preparación");
        assertEquals("En preparación", r.getDescripcion());
        r.setFecha(t);
        assertEquals(t, r.getFecha());
    }
}
