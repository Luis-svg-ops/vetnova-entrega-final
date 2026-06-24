package cl.vetnova.fichaclinica.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MedicamentoRequestTest {

    @Test
    void testMedicamentoRequest() {
        MedicamentoRequest m = new MedicamentoRequest();
        m.setNombre("Amoxicilina");
        assertEquals("Amoxicilina", m.getNombre());
        m.setDosis("1 comprimido");
        assertEquals("1 comprimido", m.getDosis());
        m.setFrecuencia("cada 8h");
        assertEquals("cada 8h", m.getFrecuencia());
        m.setDuracionDias(7);
        assertEquals(7, m.getDuracionDias());
    }
}
