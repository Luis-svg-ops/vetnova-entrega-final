package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CerrarEscalamientoRequestTest {

    @Test
    void testGettersYSetters() {
        CerrarEscalamientoRequest r = new CerrarEscalamientoRequest();
        r.setResolucion("Reembolso aprobado por gerencia");
        assertEquals("Reembolso aprobado por gerencia", r.getResolucion());
    }
}
