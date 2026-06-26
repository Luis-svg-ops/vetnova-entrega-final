package cl.vetnova.envio.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class TransferenciaResponseTest {

    @Test
    void testTransferenciaResponse() {
        TransferenciaResponse transferenciaResponse = new TransferenciaResponse();
        transferenciaResponse.setId(1L);
        assertEquals(1L, transferenciaResponse.getId());
        transferenciaResponse.setIdProducto(1L);
        assertEquals(1L, transferenciaResponse.getIdProducto());
        transferenciaResponse.setIdSucursalOrigen("CHILLAN");
        assertEquals("CHILLAN", transferenciaResponse.getIdSucursalOrigen());
        transferenciaResponse.setIdSucursalDestino("LOS_ANGELES");
        assertEquals("LOS_ANGELES", transferenciaResponse.getIdSucursalDestino());
        transferenciaResponse.setCantidad(1);
        assertEquals(1, transferenciaResponse.getCantidad());
        transferenciaResponse.setEstado("x");
        assertEquals("x", transferenciaResponse.getEstado());
        transferenciaResponse.setObservacion("x");
        assertEquals("x", transferenciaResponse.getObservacion());
        transferenciaResponse.setFecha(LocalDateTime.now());
        assertNotNull(transferenciaResponse.getFecha());
    }

}