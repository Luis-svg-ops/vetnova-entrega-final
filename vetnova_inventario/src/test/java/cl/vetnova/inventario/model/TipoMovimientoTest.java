package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class TipoMovimientoTest {

    @Test
    void testTipoMovimientoEnum() {
        assertEquals(TipoMovimiento.ENTRADA, TipoMovimiento.valueOf("ENTRADA"));
        assertEquals(2, TipoMovimiento.values().length);
    }

}