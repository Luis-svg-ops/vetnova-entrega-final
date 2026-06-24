package cl.vetnova.envio.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class CrearTransferenciaRequestTest {

    @Test
    void testCrearTransferenciaRequest() {
        CrearTransferenciaRequest crearTransferenciaRequest = new CrearTransferenciaRequest();
        crearTransferenciaRequest.setIdProducto(1L);
        assertEquals(1L, crearTransferenciaRequest.getIdProducto());
        crearTransferenciaRequest.setIdSucursalOrigen(1L);
        assertEquals(1L, crearTransferenciaRequest.getIdSucursalOrigen());
        crearTransferenciaRequest.setIdSucursalDestino(1L);
        assertEquals(1L, crearTransferenciaRequest.getIdSucursalDestino());
        crearTransferenciaRequest.setCantidad(1);
        assertEquals(1, crearTransferenciaRequest.getCantidad());
        crearTransferenciaRequest.setObservacion("x");
        assertEquals("x", crearTransferenciaRequest.getObservacion());
    }

}