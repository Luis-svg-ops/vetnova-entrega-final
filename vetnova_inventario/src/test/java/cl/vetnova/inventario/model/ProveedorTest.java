package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class ProveedorTest {

    @Test
    void testProveedor() {
        LocalDateTime t = LocalDateTime.now();
        LocalDate d = LocalDate.of(2026, 1, 1);
        Proveedor proveedor = new Proveedor();
        proveedor.setId(1L);
        assertEquals(1L, proveedor.getId());
        proveedor.setRut("x");
        assertEquals("x", proveedor.getRut());
        proveedor.setNombre("x");
        assertEquals("x", proveedor.getNombre());
        proveedor.setContacto("x");
        assertEquals("x", proveedor.getContacto());
        proveedor.setTelefono("x");
        assertEquals("x", proveedor.getTelefono());
        proveedor.setEmail("x");
        assertEquals("x", proveedor.getEmail());
        proveedor.setDireccion("x");
        assertEquals("x", proveedor.getDireccion());
        proveedor.setActivo(true);
        assertEquals(true, proveedor.getActivo());
    }
}
