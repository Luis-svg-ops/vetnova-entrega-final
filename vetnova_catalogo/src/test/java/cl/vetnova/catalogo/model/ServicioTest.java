package cl.vetnova.catalogo.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ServicioTest {

    @Test
    void testServicio() {
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        assertEquals(1L, servicio.getId());
        servicio.setNombre("x");
        assertEquals("x", servicio.getNombre());
        servicio.setDescripcion("x");
        assertEquals("x", servicio.getDescripcion());
        servicio.setPrecio(1.0);
        assertEquals(1.0, servicio.getPrecio());
        servicio.setDuracionMinutos(1);
        assertEquals(1, servicio.getDuracionMinutos());
        servicio.setActivo(true);
        assertEquals(true, servicio.getActivo());
        servicio.setCategoriaId(1L);
        assertEquals(1L, servicio.getCategoriaId());
    }

}