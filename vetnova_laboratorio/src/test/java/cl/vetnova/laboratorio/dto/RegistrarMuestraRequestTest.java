package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RegistrarMuestraRequestTest {

    @Test
    void testGettersYSetters() {
        RegistrarMuestraRequest r = new RegistrarMuestraRequest();
        r.setOrdenExamenId(1L);
        r.setTipo("SANGRE");
        r.setCodigoMuestra("M-001");
        r.setDescripcion("Muestra venosa");
        assertEquals(1L, r.getOrdenExamenId());
        assertEquals("SANGRE", r.getTipo());
        assertEquals("M-001", r.getCodigoMuestra());
        assertEquals("Muestra venosa", r.getDescripcion());
    }
}
