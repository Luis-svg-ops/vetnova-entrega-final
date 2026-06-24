package cl.vetnova.laboratorio.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class MuestraTest {

    @Test
    void testGettersYSetters() {
        Muestra m = new Muestra();
        m.setId(1L);
        assertEquals(1L, m.getId());
        m.setOrdenExamenId(2L);
        assertEquals(2L, m.getOrdenExamenId());
        m.setTipo("SANGRE");
        assertEquals("SANGRE", m.getTipo());
        m.setCodigoMuestra("M-001");
        assertEquals("M-001", m.getCodigoMuestra());
        m.setDescripcion("Muestra venosa");
        assertEquals("Muestra venosa", m.getDescripcion());
        LocalDateTime ahora = LocalDateTime.now();
        m.setFechaRecepcion(ahora);
        assertEquals(ahora, m.getFechaRecepcion());
        m.setEstadoProcesamiento("RECIBIDA");
        assertEquals("RECIBIDA", m.getEstadoProcesamiento());
        m.setResponsableRecepcion(3L);
        assertEquals(3L, m.getResponsableRecepcion());
    }
}
