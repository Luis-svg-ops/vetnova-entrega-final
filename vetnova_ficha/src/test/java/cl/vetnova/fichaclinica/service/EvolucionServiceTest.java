package cl.vetnova.fichaclinica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Evolucion;
import cl.vetnova.fichaclinica.repository.EvolucionRepository;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;

public class EvolucionServiceTest {

    @Mock
    private EvolucionRepository evolucionRepository;
    @Mock
    private FichaClinicaRepository fichaClinicaRepository;
    @InjectMocks
    private EvolucionService evolucionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Evolucion evolucion(Long fichaId, Long vetId, String descripcion) {
        Evolucion e = new Evolucion();
        e.setFichaId(fichaId);
        e.setVeterinarioId(vetId);
        e.setDescripcion(descripcion);
        return e;
    }

    @Test
    void testCrearFichaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> evolucionService.crear(evolucion(null, 2L, "Revisión")));
        assertEquals("El fichaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFichaInexistente() {
        when(fichaClinicaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> evolucionService.crear(evolucion(999L, 2L, "Revisión")));
        assertEquals("Ficha clínica no encontrada", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioNull() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> evolucionService.crear(evolucion(1L, null, "Revisión")));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearDescripcionNull() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> evolucionService.crear(evolucion(1L, 2L, null)));
        assertEquals("La descripción es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearDescripcionVacia() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> evolucionService.crear(evolucion(1L, 2L, "   ")));
        assertEquals("La descripción no puede estar vacía", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
        when(evolucionRepository.save(any(Evolucion.class))).thenAnswer(inv -> inv.getArgument(0));
        Evolucion creada = evolucionService.crear(evolucion(1L, 2L, "Paciente estable"));
        assertNotNull(creada.getFechaRegistro());
    }

    @Test
    void testListarPorFicha() {
        when(evolucionRepository.findByFichaIdOrderByFechaRegistroAsc(1L)).thenReturn(List.of(new Evolucion()));
        assertEquals(1, evolucionService.listarPorFicha(1L).size());
    }

    @Test
    void testListar() {
        when(evolucionRepository.findAll()).thenReturn(List.of(new Evolucion()));
        assertEquals(1, evolucionService.listar().size());
    }
}
