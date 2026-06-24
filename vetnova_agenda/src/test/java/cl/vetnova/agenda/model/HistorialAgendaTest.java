package cl.vetnova.agenda.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

public class HistorialAgendaTest {

    @Test
    void testHistorialAgenda() {
        HistorialAgenda historialAgenda = new HistorialAgenda();
        historialAgenda.setId(1L);
        assertEquals(1L, historialAgenda.getId());
        historialAgenda.setCitaId(1L);
        assertEquals(1L, historialAgenda.getCitaId());
        historialAgenda.setMascotaId(1L);
        assertEquals(1L, historialAgenda.getMascotaId());
        historialAgenda.setClienteId(1L);
        assertEquals(1L, historialAgenda.getClienteId());
        historialAgenda.setFecha(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), historialAgenda.getFecha());
        historialAgenda.setEstado("x");
        assertEquals("x", historialAgenda.getEstado());
        historialAgenda.setServicio("x");
        assertEquals("x", historialAgenda.getServicio());
    }

}