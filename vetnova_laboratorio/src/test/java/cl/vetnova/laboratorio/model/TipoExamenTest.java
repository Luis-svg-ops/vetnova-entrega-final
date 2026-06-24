package cl.vetnova.laboratorio.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TipoExamenTest {

    @Test
    void testGettersYSetters() {
        TipoExamen t = new TipoExamen();
        t.setId(1L);
        assertEquals(1L, t.getId());
        t.setNombre("Hemograma");
        assertEquals("Hemograma", t.getNombre());
        t.setDescripcion("Examen de sangre");
        assertEquals("Examen de sangre", t.getDescripcion());
        t.setTiempoEstimadoHoras(2);
        assertEquals(2, t.getTiempoEstimadoHoras());
        t.setRequiereMuestra(true);
        assertTrue(t.getRequiereMuestra());
        t.setInstrucciones("Ayuno de 8h");
        assertEquals("Ayuno de 8h", t.getInstrucciones());
    }
}
