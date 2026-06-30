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

    // @Mock: repositorios simulados — FichaClinicaService no llama a ningún MS externo,
    // todas las validaciones son contra BD local (mascotaRepository y fichaClinicaRepository)
    @Mock
    private FichaClinicaRepository fichaClinicaRepository;

    @Mock
    private MascotaRepository mascotaRepository;  // usado para verificar que la mascota exista antes de crear ficha

    // @InjectMocks: crea FichaClinicaService real con los dos mocks inyectados
    @InjectMocks
    private FichaClinicaService fichaClinicaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper: construye una FichaClinica con el mascotaId dado
    private FichaClinica ficha(Long mascotaId) {
        FichaClinica f = new FichaClinica();
        f.setMascotaId(mascotaId);
        return f;
    }

    // ═══════════════════════════════════════════════════════
    //  CREAR FICHA CLÍNICA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: mascotaId es obligatorio — sin él no se puede asociar la ficha a ninguna mascota.
     *
     * GIVEN: ficha con mascotaId = null
     * WHEN:  crear(ficha)
     * THEN:  BusinessRuleException "El mascotaId es obligatorio"
     *        (falla antes de consultar la BD)
     */
    @Test
    void testCrearMascotaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> fichaClinicaService.crear(ficha(null)));
        assertEquals("El mascotaId es obligatorio", ex.getMessage());
    }

    /**
     * Regla: la mascota debe existir en BD antes de crearle una ficha.
     *        No tiene sentido tener una ficha clínica de una mascota que no existe.
     *
     * GIVEN: mascotaRepository.existsById(999L) → false (mascota no registrada)
     * WHEN:  crear(ficha(999L))
     * THEN:  ResourceNotFoundException "Mascota no encontrada"
     *        (la ficha no se crea si la mascota no existe)
     */
    @Test
    void testCrearMascotaInexistente() {
        when(mascotaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> fichaClinicaService.crear(ficha(999L)));
        assertEquals("Mascota no encontrada", ex.getMessage());
    }

    /**
     * Regla: cada mascota tiene exactamente una ficha clínica — relación 1:1 estricta.
     *        Un segundo intento de crear ficha para la misma mascota debe fallar.
     * Por qué este caso: es la regla de negocio más importante de este servicio.
     *        Dos fichas para la misma mascota fragmentarían el historial clínico.
     *
     * GIVEN: mascota id=1 existe (existsById → true)
     *        fichaClinicaRepository.existsByMascotaId(1L) → true (ya tiene ficha)
     * WHEN:  crear(ficha(1L))
     * THEN:  ConflictException "La mascota ya tiene una ficha clínica" (HTTP 409)
     *        Nota: normalmente esto no ocurre porque MascotaService.crear() ya crea la ficha
     *        automáticamente — este test cubre el acceso directo a POST /fichas
     */
    @Test
    void testCrearMascotaYaTieneFicha() {
        when(mascotaRepository.existsById(1L)).thenReturn(true);
        when(fichaClinicaRepository.existsByMascotaId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> fichaClinicaService.crear(ficha(1L)));
        assertEquals("La mascota ya tiene una ficha clínica", ex.getMessage());
    }

    /**
     * Regla: caso feliz — mascota existe y no tiene ficha → se crea y se le asigna fechaCreacion automática.
     *
     * GIVEN: mascota id=1 existe, no tiene ficha aún
     *        Mock: fichaClinicaRepository.save devuelve la ficha que recibe
     * WHEN:  crear(ficha(1L))
     * THEN:  ficha creada con fechaCreacion ≠ null — la fecha se asigna internamente en el service
     */
    @Test
    void testCrearCasoFeliz() {
        when(mascotaRepository.existsById(1L)).thenReturn(true);
        when(fichaClinicaRepository.existsByMascotaId(1L)).thenReturn(false);
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenAnswer(inv -> inv.getArgument(0));
        FichaClinica creada = fichaClinicaService.crear(ficha(1L));
        assertNotNull(creada.getFechaCreacion());
    }

    // ═══════════════════════════════════════════════════════
    //  BUSCAR POR MASCOTA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: se puede buscar la ficha directamente por el id de la mascota (sin conocer el id de la ficha).
     *
     * GIVEN: fichaClinicaRepository.findByMascotaId(1L) → Optional con ficha de mascota 1
     * WHEN:  buscarPorMascota(1L)
     * THEN:  FichaClinicaResponse con mascotaId = 1
     */
    @Test
    void testBuscarPorMascota() {
        when(fichaClinicaRepository.findByMascotaId(1L)).thenReturn(Optional.of(ficha(1L)));
        assertEquals(1L, fichaClinicaService.buscarPorMascota(1L).mascotaId());
    }

    /**
     * Regla: buscar la ficha de una mascota que no existe (o no tiene ficha) lanza 404.
     *
     * GIVEN: fichaClinicaRepository.findByMascotaId(99L) → Optional.empty()
     * WHEN:  buscarPorMascota(99L)
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testBuscarPorMascotaInexistenteLanzaNotFound() {
        when(fichaClinicaRepository.findByMascotaId(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> fichaClinicaService.buscarPorMascota(99L));
    }

    // ═══════════════════════════════════════════════════════
    //  OBTENER POR ID / LISTAR
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: buscar por id de ficha devuelve la ficha si existe.
     *
     * GIVEN: fichaClinicaRepository.findById(1L) → Optional con ficha de mascota 1
     * WHEN:  obtenerPorId(1L)
     * THEN:  FichaClinicaResponse con mascotaId = 1
     */
    @Test
    void testObtenerPorIdExistente() {
        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.of(ficha(1L)));
        assertEquals(1L, fichaClinicaService.obtenerPorId(1L).mascotaId());
    }

    /**
     * Regla: buscar una ficha que no existe por id lanza 404, no devuelve null.
     *
     * GIVEN: fichaClinicaRepository.findById(99L) → Optional.empty()
     * WHEN:  obtenerPorId(99L)
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(fichaClinicaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> fichaClinicaService.obtenerPorId(99L));
    }

    /**
     * Regla: listar() devuelve todas las fichas como DTOs.
     *
     * GIVEN: fichaClinicaRepository.findAll() → lista con 1 ficha
     * WHEN:  listar()
     * THEN:  lista de tamaño 1
     */
    @Test
    void testListar() {
        when(fichaClinicaRepository.findAll()).thenReturn(List.of(new FichaClinica()));
        assertEquals(1, fichaClinicaService.listar().size());
    }
}
