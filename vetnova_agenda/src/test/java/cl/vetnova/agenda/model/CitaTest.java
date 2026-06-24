package cl.vetnova.agenda.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class CitaTest {

    @Test
    void testCita() {
        LocalDateTime t = LocalDateTime.of(2027, 6, 15, 10, 0);
        Cita cita = new Cita();
        cita.setId(1L);
        assertEquals(1L, cita.getId());
        cita.setClienteId(2L);
        assertEquals(2L, cita.getClienteId());
        cita.setMascotaId(3L);
        assertEquals(3L, cita.getMascotaId());
        cita.setVeterinarioId(4L);
        assertEquals(4L, cita.getVeterinarioId());
        cita.setServicioId(5L);
        assertEquals(5L, cita.getServicioId());
        cita.setBoxId(6L);
        assertEquals(6L, cita.getBoxId());
        cita.setSucursal("SANTIAGO");
        assertEquals("SANTIAGO", cita.getSucursal());
        cita.setFechaHora(t);
        assertEquals(t, cita.getFechaHora());
        cita.setDuracionMinutos(30);
        assertEquals(30, cita.getDuracionMinutos());
        cita.setEstado("pendiente");
        assertEquals("pendiente", cita.getEstado());
        cita.setMotivoCancelacion("x");
        assertEquals("x", cita.getMotivoCancelacion());
        cita.setCanal("WEB");
        assertEquals("WEB", cita.getCanal());
        cita.setFechaCreacion(t);
        assertEquals(t, cita.getFechaCreacion());
    }
}
