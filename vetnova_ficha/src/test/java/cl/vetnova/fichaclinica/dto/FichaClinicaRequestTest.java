package cl.vetnova.fichaclinica.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;

import org.junit.jupiter.api.Test;

public class FichaClinicaRequestTest {

    @Test
    void testFichaClinicaRequestExponeSusComponentes() {
        Date fecha = Date.valueOf("2026-06-18");
        FichaClinicaRequest request = new FichaClinicaRequest(7L, fecha, "Paciente sano");

        assertEquals(7L, request.mascotaId());
        assertEquals(fecha, request.fechaCreacion());
        assertEquals("Paciente sano", request.observacionesGenerales());
    }
}
