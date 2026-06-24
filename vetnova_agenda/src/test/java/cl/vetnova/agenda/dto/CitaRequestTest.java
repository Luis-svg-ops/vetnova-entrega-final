package cl.vetnova.agenda.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class CitaRequestTest {

    @Test
    void testCitaRequestExponeSusComponentes() {
        LocalDateTime fecha = LocalDateTime.of(2027, 6, 15, 10, 0);
        CitaRequest request = new CitaRequest(2L, 1L, 4L, 3L, 6L, "SANTIAGO", fecha, 30, "WEB");

        assertEquals(2L, request.clienteId());
        assertEquals(1L, request.mascotaId());
        assertEquals(4L, request.veterinarioId());
        assertEquals(3L, request.servicioId());
        assertEquals(6L, request.boxId());
        assertEquals("SANTIAGO", request.sucursal());
        assertEquals(fecha, request.fechaHora());
        assertEquals(30, request.duracionMinutos());
        assertEquals("WEB", request.canal());
    }
}
