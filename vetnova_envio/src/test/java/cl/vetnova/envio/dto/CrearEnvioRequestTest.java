package cl.vetnova.envio.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class CrearEnvioRequestTest {

    @Test
    void testCrearEnvioRequest() {
        CrearEnvioRequest crearEnvioRequest = new CrearEnvioRequest();
        crearEnvioRequest.setOrdenId(1L);
        assertEquals(1L, crearEnvioRequest.getOrdenId());
        crearEnvioRequest.setTipoEnvio("x");
        assertEquals("x", crearEnvioRequest.getTipoEnvio());
        crearEnvioRequest.setIdSucursalOrigen(1L);
        assertEquals(1L, crearEnvioRequest.getIdSucursalOrigen());
        crearEnvioRequest.setDireccionEntrega("x");
        assertEquals("x", crearEnvioRequest.getDireccionEntrega());
    }

}