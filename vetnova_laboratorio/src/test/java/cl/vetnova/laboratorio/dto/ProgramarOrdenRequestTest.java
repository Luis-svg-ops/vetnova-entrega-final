package cl.vetnova.laboratorio.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ProgramarOrdenRequestTest {

    @Test
    void testGettersYSetters() {
        ProgramarOrdenRequest r = new ProgramarOrdenRequest();
        LocalDateTime f = LocalDateTime.now();
        r.setFechaProgramada(f);
        assertEquals(f, r.getFechaProgramada());
    }
}
