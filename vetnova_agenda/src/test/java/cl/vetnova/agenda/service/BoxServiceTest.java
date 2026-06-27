package cl.vetnova.agenda.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.Box;
import cl.vetnova.agenda.repository.BoxRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BoxServiceTest {

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private BoxService boxService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Box box(boolean disponible) {
        Box b = new Box();
        b.setNombre("Box 1");
        b.setSucursal("Chillán");
        b.setDisponible(disponible);
        return b;
    }

    @Test
    void testReservarDejaElBoxNoDisponible() {
        when(boxRepository.findById(1L)).thenReturn(Optional.of(box(true)));
        when(boxRepository.save(any(Box.class))).thenAnswer(inv -> inv.getArgument(0));

        assertFalse(boxService.reservar(1L).getDisponible());
    }

    @Test
    void testLiberarDejaElBoxDisponible() {
        when(boxRepository.findById(1L)).thenReturn(Optional.of(box(false)));
        when(boxRepository.save(any(Box.class))).thenAnswer(inv -> inv.getArgument(0));

        assertTrue(boxService.liberar(1L).getDisponible());
    }

    @Test
    void testReservarBoxInexistenteLanzaNotFound() {
        when(boxRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> boxService.reservar(99L));
    }

    @Test
    void testLiberarBoxInexistenteLanzaNotFound() {
        when(boxRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> boxService.liberar(99L));
    }

    @Test
    void testCrearYListarBoxes() {
        Box b = new Box();
        b.setNombre("Box 1");
        b.setSucursal("CHILLAN");
        when(boxRepository.save(any(Box.class))).thenAnswer(inv -> inv.getArgument(0));
        when(boxRepository.findAll()).thenReturn(List.of(b));

        assertEquals("Box 1", boxService.crear(b).getNombre());
        assertEquals(1, boxService.listar().size());
        verify(boxRepository).save(any(Box.class));
    }

    @Test
    void testEliminarBoxLlamaAlRepositorio() {
        boxService.eliminar(1L);

        verify(boxRepository).deleteById(1L);
    }

    @Test
    void testCrearNombreNullLanzaException() {
        Box b = new Box();
        b.setSucursal("CHILLAN");
        assertThrows(BusinessRuleException.class, () -> boxService.crear(b));
    }

    @Test
    void testCrearNombreBlankLanzaException() {
        Box b = new Box();
        b.setNombre("   ");
        b.setSucursal("CHILLAN");
        assertThrows(BusinessRuleException.class, () -> boxService.crear(b));
    }

    @Test
    void testCrearSucursalNullLanzaException() {
        Box b = new Box();
        b.setNombre("Box 1");
        assertThrows(BusinessRuleException.class, () -> boxService.crear(b));
    }

    @Test
    void testCrearSucursalBlankLanzaException() {
        Box b = new Box();
        b.setNombre("Box 1");
        b.setSucursal("  ");
        assertThrows(BusinessRuleException.class, () -> boxService.crear(b));
    }

    @Test
    void testReservarBoxYaReservadoLanzaException() {
        when(boxRepository.findById(1L)).thenReturn(Optional.of(box(false)));
        assertThrows(BusinessRuleException.class, () -> boxService.reservar(1L));
    }

    @Test
    void testLiberarBoxYaDisponibleLanzaException() {
        when(boxRepository.findById(1L)).thenReturn(Optional.of(box(true)));
        assertThrows(BusinessRuleException.class, () -> boxService.liberar(1L));
    }
}
