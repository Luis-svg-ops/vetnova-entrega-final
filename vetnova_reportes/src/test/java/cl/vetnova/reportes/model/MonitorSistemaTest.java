package cl.vetnova.reportes.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class MonitorSistemaTest {

    @Test
    void testGettersYSetters() {
        MonitorSistema m = new MonitorSistema();
        m.setId(1L);
        assertEquals(1L, m.getId());
        m.setMicroservicio("MS1");
        assertEquals("MS1", m.getMicroservicio());
        m.setEstado("UP");
        assertEquals("UP", m.getEstado());
        m.setLatenciaMs(120);
        assertEquals(120, m.getLatenciaMs());
        m.setUsoCpu(45.0);
        assertEquals(45.0, m.getUsoCpu());
        m.setUsoMemoria(60.0);
        assertEquals(60.0, m.getUsoMemoria());
        LocalDateTime ahora = LocalDateTime.now();
        m.setUltimoChequeo(ahora);
        assertEquals(ahora, m.getUltimoChequeo());
    }
}
