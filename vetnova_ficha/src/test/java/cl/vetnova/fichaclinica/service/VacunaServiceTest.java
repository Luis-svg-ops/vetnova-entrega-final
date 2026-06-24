package cl.vetnova.fichaclinica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Vacuna;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.VacunaRepository;

public class VacunaServiceTest {

    @Mock
    private VacunaRepository vacunaRepository;
    @Mock
    private FichaClinicaRepository fichaClinicaRepository;
    @InjectMocks
    private VacunaService vacunaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Vacuna vacuna(Long fichaId, String nombre, String fechaAplicacion, String proximaDosis) {
        Vacuna v = new Vacuna();
        v.setFichaId(fichaId);
        v.setNombre(nombre);
        v.setFechaAplicacion(fechaAplicacion == null ? null : Date.valueOf(fechaAplicacion));
        v.setFechaProximaDosis(proximaDosis == null ? null : Date.valueOf(proximaDosis));
        return v;
    }

    private void fichaExiste() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    void testCrearFichaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> vacunaService.crear(vacuna(null, "Rabia", "2025-06-01", null)));
        assertEquals("El fichaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFichaInexistente() {
        when(fichaClinicaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vacunaService.crear(vacuna(999L, "Rabia", "2025-06-01", null)));
        assertEquals("Ficha clínica no encontrada", ex.getMessage());
    }

    @Test
    void testCrearNombreNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> vacunaService.crear(vacuna(1L, null, "2025-06-01", null)));
        assertEquals("El nombre de la vacuna es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacio() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> vacunaService.crear(vacuna(1L, "  ", "2025-06-01", null)));
        assertEquals("El nombre de la vacuna no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearFechaAplicacionNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> vacunaService.crear(vacuna(1L, "Rabia", null, null)));
        assertEquals("La fecha de aplicación es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearFechaAplicacionFutura() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> vacunaService.crear(vacuna(1L, "Rabia", "2099-01-01", null)));
        assertEquals("La fecha de aplicación no puede ser futura", ex.getMessage());
    }

    @Test
    void testCrearProximaDosisAnterior() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> vacunaService.crear(vacuna(1L, "Rabia", "2025-06-01", "2024-01-01")));
        assertEquals("La próxima dosis debe ser posterior a la fecha de aplicación", ex.getMessage());
    }

    @Test
    void testCrearDuplicada() {
        fichaExiste();
        when(vacunaRepository.existsByFichaIdAndNombreAndFechaAplicacion(1L, "Rabia", Date.valueOf("2025-06-01")))
                .thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> vacunaService.crear(vacuna(1L, "Rabia", "2025-06-01", null)));
        assertEquals("Ya existe un registro de esa vacuna para esa fecha en esta ficha", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizConProximaDosis() {
        fichaExiste();
        when(vacunaRepository.save(any(Vacuna.class))).thenAnswer(inv -> inv.getArgument(0));
        Vacuna creada = vacunaService.crear(vacuna(1L, "Rabia", "2025-06-01", "2026-06-01"));
        assertEquals("Rabia", creada.getNombre());
    }

    @Test
    void testCrearCasoFelizSinProximaDosis() {
        fichaExiste();
        when(vacunaRepository.save(any(Vacuna.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(vacunaService.crear(vacuna(1L, "Rabia", "2025-06-01", null)));
    }

    @Test
    void testListarPorFicha() {
        when(vacunaRepository.findByFichaIdOrderByFechaAplicacionAsc(1L)).thenReturn(List.of(new Vacuna()));
        assertEquals(1, vacunaService.listarPorFicha(1L).size());
    }

    @Test
    void testListar() {
        when(vacunaRepository.findAll()).thenReturn(List.of(new Vacuna()));
        assertEquals(1, vacunaService.listar().size());
    }
}
