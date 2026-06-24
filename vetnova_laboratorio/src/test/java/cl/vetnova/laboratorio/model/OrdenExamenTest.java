package cl.vetnova.laboratorio.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class OrdenExamenTest {

    @Test
    void testGettersYSetters() {
        OrdenExamen o = new OrdenExamen();
        o.setId(1L);
        assertEquals(1L, o.getId());
        o.setMascotaId(2L);
        assertEquals(2L, o.getMascotaId());
        o.setVeterinarioId(3L);
        assertEquals(3L, o.getVeterinarioId());
        TipoExamen t = new TipoExamen();
        o.setTipoExamen(t);
        assertEquals(t, o.getTipoExamen());
        o.setDescripcion("Hemograma completo");
        assertEquals("Hemograma completo", o.getDescripcion());
        o.setEstado("SOLICITADA");
        assertEquals("SOLICITADA", o.getEstado());
        LocalDateTime ahora = LocalDateTime.now();
        o.setFechaSolicitud(ahora);
        assertEquals(ahora, o.getFechaSolicitud());
        o.setFechaProgramada(ahora);
        assertEquals(ahora, o.getFechaProgramada());
        o.setMotivoCancelacion("Paciente no asistió");
        assertEquals("Paciente no asistió", o.getMotivoCancelacion());
    }
}
