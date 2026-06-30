package cl.vetnova.agenda.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import cl.vetnova.agenda.model.BloqueAgenda;
import cl.vetnova.agenda.repository.BloqueAgendaRepository;
import cl.vetnova.agenda.repository.CitaRepository;

public class BloqueAgendaServiceTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2030, 7, 1, 9, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2030, 7, 1, 17, 0);

    @Mock
    private BloqueAgendaRepository bloqueAgendaRepository;
    @Mock
    private CitaRepository citaRepository;
    @InjectMocks
    private BloqueAgendaService bloqueAgendaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private BloqueAgenda bloque(Long vet, LocalDateTime inicio, LocalDateTime fin, String motivo, Long creadoPor) {
        BloqueAgenda b = new BloqueAgenda();
        b.setVeterinarioId(vet);
        b.setFechaInicio(inicio);
        b.setFechaFin(fin);
        b.setMotivo(motivo);
        b.setCreadoPor(creadoPor);
        return b;
    }

    private BloqueAgenda valido() {
        return bloque(4L, INICIO, FIN, "Vacaciones", 2L);
    }

    private void sinCitas() {
        when(citaRepository.existsByVeterinarioIdAndEstadoAndFechaHoraBetween(
                anyLong(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
    }

    // ---- crear ----

    @Test
    void testCrearVeterinarioNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(null, INICIO, FIN, "x", 2L)));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFechaInicioNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, null, FIN, "x", 2L)));
        assertEquals("La fecha de inicio es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearFechaInicioEnPasado() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, LocalDateTime.of(2020, 1, 1, 9, 0), FIN, "x", 2L)));
        assertEquals("La fecha de inicio no puede ser en el pasado", ex.getMessage());
    }

    @Test
    void testCrearFechaFinNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, INICIO, null, "x", 2L)));
        assertEquals("La fecha de fin es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearFechaFinMenorAInicio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, FIN, INICIO, "x", 2L)));
        assertEquals("La fecha de fin debe ser posterior a la de inicio", ex.getMessage());
    }

    @Test
    void testCrearFechaFinIgualAInicio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, INICIO, INICIO, "x", 2L)));
        assertEquals("La fecha de fin debe ser posterior", ex.getMessage());
    }

    @Test
    void testCrearMotivoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, INICIO, FIN, null, 2L)));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMotivoVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, INICIO, FIN, "   ", 2L)));
        assertEquals("El motivo no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearCreadoPorNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> bloqueAgendaService.crear(bloque(4L, INICIO, FIN, "Vacaciones", null)));
        assertEquals("El creador es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearSolapadoConCitasConfirmadas() {
        when(citaRepository.existsByVeterinarioIdAndEstadoAndFechaHoraBetween(
                eq(4L), eq("confirmada"), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> bloqueAgendaService.crear(valido()));
        assertEquals("Existen citas en el período. Cancele o reagende primero", ex.getMessage());
    }

    @Test
    void testCrearSolapadoConOtroBloqueo() {
        sinCitas();
        when(bloqueAgendaRepository.findByVeterinarioId(4L))
                .thenReturn(List.of(bloque(4L, INICIO.plusMinutes(30), FIN.plusHours(1), "Otro", 2L)));
        ConflictException ex = assertThrows(ConflictException.class, () -> bloqueAgendaService.crear(valido()));
        assertEquals("Ya existe bloqueo en ese período", ex.getMessage());
    }

    @Test
    void testCrearSinSolapamientoBloqueoAnterior() {
        sinCitas();
        when(bloqueAgendaRepository.findByVeterinarioId(4L))
                .thenReturn(List.of(bloque(4L, INICIO.minusDays(1), INICIO.minusHours(1), "Previo", 2L)));
        when(bloqueAgendaRepository.save(any(BloqueAgenda.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(4L, bloqueAgendaService.crear(valido()).getVeterinarioId());
    }

    @Test
    void testCrearSinSolapamientoBloqueoPosterior() {
        sinCitas();
        when(bloqueAgendaRepository.findByVeterinarioId(4L))
                .thenReturn(List.of(bloque(4L, FIN.plusHours(1), FIN.plusHours(2), "Posterior", 2L)));
        when(bloqueAgendaRepository.save(any(BloqueAgenda.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(4L, bloqueAgendaService.crear(valido()).getVeterinarioId());
    }

    @Test
    void testCrearCasoFeliz() {
        sinCitas();
        when(bloqueAgendaRepository.findByVeterinarioId(4L)).thenReturn(List.of());
        when(bloqueAgendaRepository.save(any(BloqueAgenda.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(4L, bloqueAgendaService.crear(valido()).getVeterinarioId());
    }

    // ---- eliminar (CA-BLO-16, 17) ----

    @Test
    void testEliminarBloqueoEnCursoImpedido() {
        LocalDateTime ahora = LocalDateTime.now();
        BloqueAgenda enCurso = bloque(4L, ahora.minusHours(1), ahora.plusHours(1), "x", 2L);
        when(bloqueAgendaRepository.findById(1L)).thenReturn(Optional.of(enCurso));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> bloqueAgendaService.eliminar(1L));
        assertEquals("No se puede eliminar bloqueo en curso", ex.getMessage());
        verify(bloqueAgendaRepository, never()).deleteById(any());
    }

    @Test
    void testEliminarBloqueoFuturo() {
        when(bloqueAgendaRepository.findById(1L)).thenReturn(Optional.of(valido()));
        bloqueAgendaService.eliminar(1L);
        verify(bloqueAgendaRepository).deleteById(1L);
    }

    @Test
    void testEliminarBloqueoYaPasado() {
        LocalDateTime ahora = LocalDateTime.now();
        BloqueAgenda pasado = bloque(4L, ahora.minusDays(2), ahora.minusDays(1), "x", 2L);
        when(bloqueAgendaRepository.findById(1L)).thenReturn(Optional.of(pasado));
        bloqueAgendaService.eliminar(1L);
        verify(bloqueAgendaRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(bloqueAgendaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bloqueAgendaService.eliminar(99L));
    }

    // ---- listar / obtener ----

    @Test
    void testListar() {
        when(bloqueAgendaRepository.findAll()).thenReturn(List.of(new BloqueAgenda()));
        assertEquals(1, bloqueAgendaService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(bloqueAgendaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bloqueAgendaService.obtenerPorId(99L));
    }

    @Test
    void testObtenerPorIdExistenteDevuelveElBloque() {
        when(bloqueAgendaRepository.findById(1L)).thenReturn(Optional.of(valido()));
        assertEquals("Vacaciones", bloqueAgendaService.obtenerPorId(1L).getMotivo());
    }
}
