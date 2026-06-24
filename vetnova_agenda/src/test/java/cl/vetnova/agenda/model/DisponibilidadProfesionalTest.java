package cl.vetnova.agenda.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

public class DisponibilidadProfesionalTest {

    @Test
    void testDisponibilidadProfesional() {
        DisponibilidadProfesional disponibilidadProfesional = new DisponibilidadProfesional();
        disponibilidadProfesional.setId(1L);
        assertEquals(1L, disponibilidadProfesional.getId());
        disponibilidadProfesional.setVeterinarioId(1L);
        assertEquals(1L, disponibilidadProfesional.getVeterinarioId());
        disponibilidadProfesional.setDiaSemana("x");
        assertEquals("x", disponibilidadProfesional.getDiaSemana());
        disponibilidadProfesional.setHoraInicio("x");
        assertEquals("x", disponibilidadProfesional.getHoraInicio());
        disponibilidadProfesional.setHoraFin("x");
        assertEquals("x", disponibilidadProfesional.getHoraFin());
        disponibilidadProfesional.setSucursal("x");
        assertEquals("x", disponibilidadProfesional.getSucursal());
        disponibilidadProfesional.setActiva(true);
        assertEquals(true, disponibilidadProfesional.getActiva());
    }

}