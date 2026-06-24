package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RecepcionMuestraRequestTest {

    @Test
    void testGettersYSetters() {
        RecepcionMuestraRequest r = new RecepcionMuestraRequest();
        r.setResponsableId(3L);
        assertEquals(3L, r.getResponsableId());
    }
}
