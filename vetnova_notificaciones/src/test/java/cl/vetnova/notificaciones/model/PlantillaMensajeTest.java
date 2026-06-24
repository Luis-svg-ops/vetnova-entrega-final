package cl.vetnova.notificaciones.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PlantillaMensajeTest {

    @Test
    void testGettersYSetters() {
        PlantillaMensaje p = new PlantillaMensaje();
        p.setId(1L);
        assertEquals(1L, p.getId());
        p.setNombre("Recordatorio Cita");
        assertEquals("Recordatorio Cita", p.getNombre());
        p.setTipo("EMAIL");
        assertEquals("EMAIL", p.getTipo());
        p.setContenido("Hola {{nombre}}");
        assertEquals("Hola {{nombre}}", p.getContenido());
        p.setActiva(true);
        assertTrue(p.getActiva());
    }
}
