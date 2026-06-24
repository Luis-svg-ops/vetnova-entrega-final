package cl.vetnova.fichaclinica.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class MascotaTest {

    @Test
    void testMascota() {
        Mascota mascota = new Mascota();
        mascota.setId(1L);
        assertEquals(1L, mascota.getId());
        mascota.setClienteId(2L);
        assertEquals(2L, mascota.getClienteId());
        mascota.setNombre("Firulais");
        assertEquals("Firulais", mascota.getNombre());
        mascota.setEspecie("Canino");
        assertEquals("Canino", mascota.getEspecie());
        mascota.setRaza("Labrador");
        assertEquals("Labrador", mascota.getRaza());
        mascota.setSexo("Macho");
        assertEquals("Macho", mascota.getSexo());
        LocalDate nacimiento = LocalDate.of(2020, 5, 10);
        mascota.setFechaNacimiento(nacimiento);
        assertEquals(nacimiento, mascota.getFechaNacimiento());
        mascota.setPeso(28.5);
        assertEquals(28.5, mascota.getPeso());
        mascota.setMicrochip("CHIP-123456");
        assertEquals("CHIP-123456", mascota.getMicrochip());
        mascota.setActivo(true);
        assertEquals(true, mascota.getActivo());
    }
}
