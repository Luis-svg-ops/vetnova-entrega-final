package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class TipoEnvioTest {

    @Test
    void testTipoEnvioEnum() {
        assertEquals(TipoEnvio.DOMICILIO, TipoEnvio.valueOf("DOMICILIO"));
        assertEquals(2, TipoEnvio.values().length);
    }

}