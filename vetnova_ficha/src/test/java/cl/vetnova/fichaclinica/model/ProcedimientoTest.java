package cl.vetnova.fichaclinica.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ProcedimientoTest {

    @Test
    void testProcedimiento() {
        Procedimiento procedimiento = new Procedimiento();
        procedimiento.setId(1L);
        assertEquals(1L, procedimiento.getId());
        procedimiento.setFichaId(1L);
        assertEquals(1L, procedimiento.getFichaId());
        procedimiento.setVeterinarioId(1L);
        assertEquals(1L, procedimiento.getVeterinarioId());
        procedimiento.setTipo("x");
        assertEquals("x", procedimiento.getTipo());
        procedimiento.setDescripcion("x");
        assertEquals("x", procedimiento.getDescripcion());
        procedimiento.setFecha(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), procedimiento.getFecha());
        procedimiento.setResultado("x");
        assertEquals("x", procedimiento.getResultado());
        procedimiento.setNombre("Castración");
        assertEquals("Castración", procedimiento.getNombre());
        LocalDateTime t = LocalDateTime.now();
        procedimiento.setFechaRegistro(t);
        assertEquals(t, procedimiento.getFechaRegistro());
    }

}