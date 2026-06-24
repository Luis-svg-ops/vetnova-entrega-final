package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CancelarOrdenRequestTest {

    @Test
    void testGettersYSetters() {
        CancelarOrdenRequest r = new CancelarOrdenRequest();
        r.setMotivo("Paciente no asistió");
        assertEquals("Paciente no asistió", r.getMotivo());
    }
}
