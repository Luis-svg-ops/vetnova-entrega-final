package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TipoExamenRequestTest {

    @Test
    void testGettersYSetters() {
        TipoExamenRequest r = new TipoExamenRequest();
        r.setNombre("Hemograma");
        r.setDescripcion("Examen de sangre");
        r.setTiempoEstimadoHoras(2);
        r.setRequiereMuestra(true);
        r.setInstrucciones("Ayuno de 8h");
        assertEquals("Hemograma", r.getNombre());
        assertEquals("Examen de sangre", r.getDescripcion());
        assertEquals(2, r.getTiempoEstimadoHoras());
        assertTrue(r.getRequiereMuestra());
        assertEquals("Ayuno de 8h", r.getInstrucciones());
    }
}
