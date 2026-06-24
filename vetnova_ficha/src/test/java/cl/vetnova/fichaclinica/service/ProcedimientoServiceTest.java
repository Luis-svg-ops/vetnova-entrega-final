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
import cl.vetnova.fichaclinica.model.Procedimiento;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.ProcedimientoRepository;

public class ProcedimientoServiceTest {

    @Mock
    private ProcedimientoRepository procedimientoRepository;
    @Mock
    private FichaClinicaRepository fichaClinicaRepository;
    @InjectMocks
    private ProcedimientoService procedimientoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Procedimiento procedimiento(Long fichaId, String nombre, String descripcion, Long vetId) {
        Procedimiento p = new Procedimiento();
        p.setFichaId(fichaId);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setVeterinarioId(vetId);
        return p;
    }

    private void fichaExiste() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    void testCrearFichaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> procedimientoService.crear(procedimiento(null, "Castración", "Ok", 2L)));
        assertEquals("El fichaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFichaInexistente() {
        when(fichaClinicaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> procedimientoService.crear(procedimiento(999L, "Castración", "Ok", 2L)));
        assertEquals("Ficha clínica no encontrada", ex.getMessage());
    }

    @Test
    void testCrearNombreNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> procedimientoService.crear(procedimiento(1L, null, "Ok", 2L)));
        assertEquals("El nombre del procedimiento es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacio() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> procedimientoService.crear(procedimiento(1L, "  ", "Ok", 2L)));
        assertEquals("El nombre del procedimiento no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearDescripcionNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> procedimientoService.crear(procedimiento(1L, "Castración", null, 2L)));
        assertEquals("La descripción es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearDescripcionVacia() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> procedimientoService.crear(procedimiento(1L, "Castración", "  ", 2L)));
        assertEquals("La descripción no puede estar vacía", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> procedimientoService.crear(procedimiento(1L, "Castración", "Ok", null)));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        fichaExiste();
        when(procedimientoRepository.save(any(Procedimiento.class))).thenAnswer(inv -> inv.getArgument(0));
        Procedimiento creado = procedimientoService.crear(procedimiento(1L, "Castración", "Sin complicaciones", 2L));
        assertNotNull(creado.getFechaRegistro());
    }

    @Test
    void testListarPorFicha() {
        when(procedimientoRepository.findByFichaIdOrderByFechaRegistroAsc(1L)).thenReturn(List.of(new Procedimiento()));
        assertEquals(1, procedimientoService.listarPorFicha(1L).size());
    }

    @Test
    void testListar() {
        when(procedimientoRepository.findAll()).thenReturn(List.of(new Procedimiento()));
        assertEquals(1, procedimientoService.listar().size());
    }
}
