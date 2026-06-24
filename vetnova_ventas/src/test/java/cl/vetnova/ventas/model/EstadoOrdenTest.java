package cl.vetnova.ventas.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class EstadoOrdenTest {

    @Test
    void testEstadoOrdenEnum() {
        assertEquals(EstadoOrden.PENDIENTE, EstadoOrden.valueOf("PENDIENTE"));
        assertEquals(5, EstadoOrden.values().length);
    }

}