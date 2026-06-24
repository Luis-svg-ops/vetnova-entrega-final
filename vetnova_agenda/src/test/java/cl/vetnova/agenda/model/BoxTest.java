package cl.vetnova.agenda.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

public class BoxTest {

    @Test
    void testBox() {
        Box box = new Box();
        box.setId(1L);
        assertEquals(1L, box.getId());
        box.setNombre("x");
        assertEquals("x", box.getNombre());
        box.setSucursal("x");
        assertEquals("x", box.getSucursal());
        box.setDisponible(true);
        assertEquals(true, box.getDisponible());
        box.setTipo("x");
        assertEquals("x", box.getTipo());
    }

}