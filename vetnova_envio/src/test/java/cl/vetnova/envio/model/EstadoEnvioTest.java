package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class EstadoEnvioTest {

    @Test
    void testEstadoEnvioEnum() {
        assertEquals(EstadoEnvio.PREPARANDO, EstadoEnvio.valueOf("PREPARANDO"));
        assertEquals(4, EstadoEnvio.values().length);
    }

}