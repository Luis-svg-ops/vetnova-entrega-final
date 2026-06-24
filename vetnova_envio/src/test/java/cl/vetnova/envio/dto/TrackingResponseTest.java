package cl.vetnova.envio.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class TrackingResponseTest {

    @Test
    void testTrackingResponse() {
        TrackingResponse trackingResponse = new TrackingResponse();
        trackingResponse.setId(1L);
        assertEquals(1L, trackingResponse.getId());
        trackingResponse.setEstado("x");
        assertEquals("x", trackingResponse.getEstado());
        trackingResponse.setObservacion("x");
        assertEquals("x", trackingResponse.getObservacion());
        trackingResponse.setFecha(LocalDateTime.now());
        assertNotNull(trackingResponse.getFecha());
    }

}