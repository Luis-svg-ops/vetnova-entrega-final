package cl.vetnova.agenda.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CancelarCitaRequestTest {

    @Test
    void testCancelarCitaRequest() {
        CancelarCitaRequest request = new CancelarCitaRequest("Emergencia");
        assertEquals("Emergencia", request.motivoCancelacion());
    }
}
