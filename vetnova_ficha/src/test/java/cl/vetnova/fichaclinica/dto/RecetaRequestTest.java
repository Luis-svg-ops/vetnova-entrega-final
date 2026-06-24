package cl.vetnova.fichaclinica.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RecetaRequestTest {

    @Test
    void testRecetaRequest() {
        RecetaRequest r = new RecetaRequest();
        r.setFichaId(1L);
        assertEquals(1L, r.getFichaId());
        r.setVeterinarioId(2L);
        assertEquals(2L, r.getVeterinarioId());
        r.setMedicamentos(List.of(new MedicamentoRequest()));
        assertEquals(1, r.getMedicamentos().size());
        Date v = Date.valueOf("2026-06-01");
        r.setFechaVencimiento(v);
        assertEquals(v, r.getFechaVencimiento());
    }
}
