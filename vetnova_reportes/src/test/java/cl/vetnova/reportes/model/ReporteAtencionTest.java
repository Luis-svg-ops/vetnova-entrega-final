package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class ReporteAtencionTest {

    @Test
    void testGettersYSetters() {
        ReporteAtencion r = new ReporteAtencion();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setReporteId(2L);
        assertEquals(2L, r.getReporteId());
        r.setTotalCitas(20);
        assertEquals(20, r.getTotalCitas());
        r.setCitasRealizadas(12);
        assertEquals(12, r.getCitasRealizadas());
        r.setCitasCanceladas(5);
        assertEquals(5, r.getCitasCanceladas());
        r.setCitasAusentes(3);
        assertEquals(3, r.getCitasAusentes());
        Map<String, Integer> vet = Map.of("2", 8, "3", 12);
        r.setAtencionPorVeterinario(vet);
        assertEquals(vet, r.getAtencionPorVeterinario());
        Map<String, Integer> serv = Map.of("Consulta", 15);
        r.setAtencionPorServicio(serv);
        assertEquals(serv, r.getAtencionPorServicio());
    }
}
