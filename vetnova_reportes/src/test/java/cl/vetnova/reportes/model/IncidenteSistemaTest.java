package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class IncidenteSistemaTest {

    @Test
    void testGettersYSetters() {
        IncidenteSistema i = new IncidenteSistema();
        i.setId(1L);
        assertEquals(1L, i.getId());
        i.setMicroservicio("MS1");
        assertEquals("MS1", i.getMicroservicio());
        i.setTipo("DOWN");
        assertEquals("DOWN", i.getTipo());
        i.setSeveridad("CRITICA");
        assertEquals("CRITICA", i.getSeveridad());
        i.setDescripcion("Servicio caído");
        assertEquals("Servicio caído", i.getDescripcion());
        i.setNotificado(false);
        assertFalse(i.getNotificado());
        i.setEstado("ABIERTO");
        assertEquals("ABIERTO", i.getEstado());
        LocalDateTime ahora = LocalDateTime.now();
        i.setFechaDeteccion(ahora);
        assertEquals(ahora, i.getFechaDeteccion());
        i.setFechaResolucion(ahora);
        assertEquals(ahora, i.getFechaResolucion());
    }
}
