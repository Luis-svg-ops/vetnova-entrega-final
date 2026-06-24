package cl.vetnova.fichaclinica.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

public class FichaClinicaTest {

    @Test
    void testFichaClinica() {
        FichaClinica fichaClinica = new FichaClinica();
        fichaClinica.setId(1L);
        assertEquals(1L, fichaClinica.getId());
        fichaClinica.setMascotaId(1L);
        assertEquals(1L, fichaClinica.getMascotaId());
        fichaClinica.setFechaCreacion(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), fichaClinica.getFechaCreacion());
        fichaClinica.setObservacionesGenerales("x");
        assertEquals("x", fichaClinica.getObservacionesGenerales());
    }

}