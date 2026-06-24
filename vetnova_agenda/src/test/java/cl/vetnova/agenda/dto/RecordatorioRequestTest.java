package cl.vetnova.agenda.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RecordatorioRequestTest {

    @Test
    void testRecordatorioRequest() {
        RecordatorioRequest request = new RecordatorioRequest(1L, "EMAIL");
        assertEquals(1L, request.citaId());
        assertEquals("EMAIL", request.tipo());
    }
}
