package cl.vetnova.fichaclinica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.MascotaRepository;

public class FichaClinicaServiceTest {

    @Mock
    private FichaClinicaRepository fichaClinicaRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @InjectMocks
    private FichaClinicaService fichaClinicaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private FichaClinica ficha(Long mascotaId) {
        FichaClinica f = new FichaClinica();
        f.setMascotaId(mascotaId);
        return f;
    }

    @Test
    void testCrearMascotaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> fichaClinicaService.crear(ficha(null)));
        assertEquals("El mascotaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMascotaInexistente() {
        when(mascotaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> fichaClinicaService.crear(ficha(999L)));
        assertEquals("Mascota no encontrada", ex.getMessage());
    }

    @Test
    void testCrearMascotaYaTieneFicha() {
        when(mascotaRepository.existsById(1L)).thenReturn(true);
        when(fichaClinicaRepository.existsByMascotaId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> fichaClinicaService.crear(ficha(1L)));
        assertEquals("La mascota ya tiene una ficha clínica", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(mascotaRepository.existsById(1L)).thenReturn(true);
        when(fichaClinicaRepository.existsByMascotaId(1L)).thenReturn(false);
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenAnswer(inv -> inv.getArgument(0));
        FichaClinica creada = fichaClinicaService.crear(ficha(1L));
        assertNotNull(creada.getFechaCreacion());
    }

    @Test
    void testBuscarPorMascota() {
        when(fichaClinicaRepository.findByMascotaId(1L)).thenReturn(Optional.of(ficha(1L)));
        assertEquals(1L, fichaClinicaService.buscarPorMascota(1L).mascotaId());
    }

    @Test
    void testBuscarPorMascotaInexistenteLanzaNotFound() {
        when(fichaClinicaRepository.findByMascotaId(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> fichaClinicaService.buscarPorMascota(99L));
    }

    @Test
    void testObtenerPorIdExistente() {
        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.of(ficha(1L)));
        assertEquals(1L, fichaClinicaService.obtenerPorId(1L).mascotaId());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(fichaClinicaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> fichaClinicaService.obtenerPorId(99L));
    }

    @Test
    void testListar() {
        when(fichaClinicaRepository.findAll()).thenReturn(List.of(new FichaClinica()));
        assertEquals(1, fichaClinicaService.listar().size());
    }
}
