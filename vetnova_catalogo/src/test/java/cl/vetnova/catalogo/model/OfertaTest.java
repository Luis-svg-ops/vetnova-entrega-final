package cl.vetnova.catalogo.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class OfertaTest {

    @Test
    void testOferta() {
        Oferta oferta = new Oferta();
        oferta.setId(1L);
        assertEquals(1L, oferta.getId());
        oferta.setProductoId(1L);
        assertEquals(1L, oferta.getProductoId());
        oferta.setDescuento(1.0);
        assertEquals(1.0, oferta.getDescuento());
        oferta.setFechaInicio(LocalDate.now());
        assertNotNull(oferta.getFechaInicio());
        oferta.setFechaFin(LocalDate.now());
        assertNotNull(oferta.getFechaFin());
        oferta.setActiva(true);
        assertEquals(true, oferta.getActiva());
    }

}