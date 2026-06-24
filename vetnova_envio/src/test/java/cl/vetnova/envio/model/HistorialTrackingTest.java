package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class HistorialTrackingTest {

    @Test
    void testHistorialTracking() {
        HistorialTracking historialTracking = new HistorialTracking();
        historialTracking.setId(1L);
        assertEquals(1L, historialTracking.getId());
        historialTracking.setEnvio(new Envio());
        assertNotNull(historialTracking.getEnvio());
        historialTracking.setEstado(EstadoEnvio.PREPARANDO);
        assertEquals(EstadoEnvio.PREPARANDO, historialTracking.getEstado());
        historialTracking.setObservacion("x");
        assertEquals("x", historialTracking.getObservacion());
        historialTracking.setFecha(LocalDateTime.now());
        assertNotNull(historialTracking.getFecha());
    }

}