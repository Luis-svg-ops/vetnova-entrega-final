package cl.vetnova.auth.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ClienteTest {

    @Test
    void testCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        assertEquals(1L, cliente.getId());
        cliente.setUsuarioId(2L);
        assertEquals(2L, cliente.getUsuarioId());
        cliente.setRut("12.345.678-9");
        assertEquals("12.345.678-9", cliente.getRut());
        cliente.setNombre("Juan");
        assertEquals("Juan", cliente.getNombre());
        cliente.setApellido("Pérez");
        assertEquals("Pérez", cliente.getApellido());
        cliente.setEmail("juan@correo.cl");
        assertEquals("juan@correo.cl", cliente.getEmail());
        cliente.setTelefono("+56912345678");
        assertEquals("+56912345678", cliente.getTelefono());
        cliente.setDireccion("Av. Siempre Viva 123");
        assertEquals("Av. Siempre Viva 123", cliente.getDireccion());
        cliente.setActivo(true);
        assertEquals(true, cliente.getActivo());
        LocalDateTime ahora = LocalDateTime.now();
        cliente.setFechaRegistro(ahora);
        assertEquals(ahora, cliente.getFechaRegistro());
    }
}
