package cl.vetnova.agenda.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class RecordatorioTest {

    @Test
    void testRecordatorio() {
        LocalDateTime t = LocalDateTime.of(2030, 6, 30, 10, 0);
        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setId(1L);
        assertEquals(1L, recordatorio.getId());
        recordatorio.setCitaId(2L);
        assertEquals(2L, recordatorio.getCitaId());
        recordatorio.setTipo("EMAIL");
        assertEquals("EMAIL", recordatorio.getTipo());
        recordatorio.setFechaEnvio(t);
        assertEquals(t, recordatorio.getFechaEnvio());
        recordatorio.setMensaje("Recordatorio");
        assertEquals("Recordatorio", recordatorio.getMensaje());
        recordatorio.setEnviado(false);
        assertEquals(false, recordatorio.getEnviado());
        recordatorio.setCancelado(false);
        assertEquals(false, recordatorio.getCancelado());
    }
}
