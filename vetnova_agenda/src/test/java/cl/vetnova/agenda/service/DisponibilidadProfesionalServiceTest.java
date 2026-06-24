package cl.vetnova.agenda.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.DisponibilidadProfesional;
import cl.vetnova.agenda.repository.CitaRepository;
import cl.vetnova.agenda.repository.DisponibilidadProfesionalRepository;

public class DisponibilidadProfesionalServiceTest {

    @Mock
    private DisponibilidadProfesionalRepository disponibilidadRepository;
    @Mock
    private CitaRepository citaRepository;
    @InjectMocks
    private DisponibilidadProfesionalService disponibilidadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private DisponibilidadProfesional horario(Long vet, String dia, String inicio, String fin, String sucursal) {
        DisponibilidadProfesional d = new DisponibilidadProfesional();
        d.setVeterinarioId(vet);
        d.setDiaSemana(dia);
        d.setHoraInicio(inicio);
        d.setHoraFin(fin);
        d.setSucursal(sucursal);
        return d;
    }

    private DisponibilidadProfesional valido() {
        return horario(4L, "LUNES", "09:00", "18:00", "SANTIAGO");
    }

    // ---- crear ----

    @Test
    void testCrearVeterinarioNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(null, "LUNES", "09:00", "18:00", "SANTIAGO")));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearDiaNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, null, "09:00", "18:00", "SANTIAGO")));
        assertEquals("El día de la semana es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearDiaInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "FUNESDAY", "09:00", "18:00", "SANTIAGO")));
        assertEquals("Día no válido. Valores: LUNES-DOMINGO", ex.getMessage());
    }

    @Test
    void testCrearHoraInicioNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", null, "18:00", "SANTIAGO")));
        assertEquals("La hora de inicio es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearHoraInicioFormatoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", "9am", "18:00", "SANTIAGO")));
        assertEquals("Formato inválido. Use HH:mm", ex.getMessage());
    }

    @Test
    void testCrearHoraFinNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", "09:00", null, "SANTIAGO")));
        assertEquals("La hora de fin es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearHoraFinFormatoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", "09:00", "6pm", "SANTIAGO")));
        assertEquals("Formato inválido. Use HH:mm", ex.getMessage());
    }

    @Test
    void testCrearHoraFinMenorAInicio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", "18:00", "09:00", "SANTIAGO")));
        assertEquals("La hora de fin debe ser posterior a la de inicio", ex.getMessage());
    }

    @Test
    void testCrearHoraFinIgualAInicio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", "09:00", "09:00", "SANTIAGO")));
        assertEquals("La hora de fin debe ser posterior", ex.getMessage());
    }

    @Test
    void testCrearSucursalNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", "09:00", "18:00", null)));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearSucursalInexistente() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> disponibilidadService.crear(horario(4L, "LUNES", "09:00", "18:00", "FANTASMA")));
        assertEquals("Sucursal no encontrada", ex.getMessage());
    }

    @Test
    void testCrearDuplicado() {
        when(disponibilidadRepository.existsByVeterinarioIdAndDiaSemanaAndSucursal(4L, "LUNES", "SANTIAGO"))
                .thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> disponibilidadService.crear(valido()));
        assertEquals("Ya existe horario para ese día y sucursal", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(disponibilidadRepository.existsByVeterinarioIdAndDiaSemanaAndSucursal(4L, "LUNES", "SANTIAGO"))
                .thenReturn(false);
        when(disponibilidadRepository.save(any(DisponibilidadProfesional.class))).thenAnswer(inv -> inv.getArgument(0));
        assertTrue(disponibilidadService.crear(valido()).getActiva());
    }

    // ---- actualizar (CA-HOR-15, 16) ----

    @Test
    void testActualizarConCitasProximas() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(valido()));
        when(citaRepository.existsByVeterinarioIdAndFechaHoraBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> disponibilidadService.actualizar(1L, horario(4L, "MARTES", "11:00", "18:00", "SANTIAGO")));
        assertEquals("No se puede modificar con citas en los próximos 7 días", ex.getMessage());
    }

    @Test
    void testActualizarSinCitas() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(valido()));
        when(citaRepository.existsByVeterinarioIdAndFechaHoraBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(disponibilidadRepository.save(any(DisponibilidadProfesional.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("11:00", disponibilidadService.actualizar(1L, horario(4L, "LUNES", "11:00", "18:00", "SANTIAGO")).getHoraInicio());
    }

    // ---- activar / desactivar ----

    @Test
    void testActivarYDesactivar() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(valido()));
        when(disponibilidadRepository.save(any(DisponibilidadProfesional.class))).thenAnswer(inv -> inv.getArgument(0));
        assertTrue(disponibilidadService.activar(1L).getActiva());
        assertFalse(disponibilidadService.desactivar(1L).getActiva());
    }

    // ---- eliminar (CA-HOR-17, 18) ----

    @Test
    void testEliminarConCitasFuturas() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(valido()));
        when(citaRepository.existsByVeterinarioIdAndFechaHoraAfter(anyLong(), any(LocalDateTime.class))).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> disponibilidadService.eliminar(1L));
        assertEquals("No se puede eliminar con citas futuras", ex.getMessage());
        verify(disponibilidadRepository, never()).deleteById(any());
    }

    @Test
    void testEliminarSinCitas() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(valido()));
        when(citaRepository.existsByVeterinarioIdAndFechaHoraAfter(anyLong(), any(LocalDateTime.class))).thenReturn(false);
        disponibilidadService.eliminar(1L);
        verify(disponibilidadRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> disponibilidadService.eliminar(99L));
    }

    // ---- listar / obtener ----

    @Test
    void testListar() {
        when(disponibilidadRepository.findAll()).thenReturn(List.of(new DisponibilidadProfesional()));
        assertEquals(1, disponibilidadService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> disponibilidadService.obtenerPorId(99L));
    }
}
