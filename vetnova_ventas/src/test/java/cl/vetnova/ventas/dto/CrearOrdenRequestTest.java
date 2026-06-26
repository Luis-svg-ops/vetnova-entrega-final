package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class CrearOrdenRequestTest {

    @Test
    void testCrearOrdenRequest() {
        CrearOrdenRequest crearOrdenRequest = new CrearOrdenRequest();
        crearOrdenRequest.setClienteId(1L);
        assertEquals(1L, crearOrdenRequest.getClienteId());
        crearOrdenRequest.setSucursal("CHILLAN");
        assertEquals("CHILLAN", crearOrdenRequest.getSucursal());
        crearOrdenRequest.setDetalles(new ArrayList<>());
        assertNotNull(crearOrdenRequest.getDetalles());
    }

}