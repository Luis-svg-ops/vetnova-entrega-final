package cl.vetnova.catalogo.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class OfertaRequestTest {

    @Test
    void testOfertaRequestExponeSusComponentes() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 2, 1);
        OfertaRequest request = new OfertaRequest(5L, 15.0, inicio, fin, true);

        assertEquals(5L, request.productoId());
        assertEquals(15.0, request.descuento());
        assertEquals(inicio, request.fechaInicio());
        assertEquals(fin, request.fechaFin());
        assertTrue(request.activa());
    }
}
