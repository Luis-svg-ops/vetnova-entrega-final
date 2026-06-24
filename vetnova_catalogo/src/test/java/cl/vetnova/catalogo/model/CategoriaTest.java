package cl.vetnova.catalogo.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class CategoriaTest {

    @Test
    void testCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        assertEquals(1L, categoria.getId());
        categoria.setNombre("x");
        assertEquals("x", categoria.getNombre());
        categoria.setDescripcion("x");
        assertEquals("x", categoria.getDescripcion());
        categoria.setTipo("x");
        assertEquals("x", categoria.getTipo());
    }

}