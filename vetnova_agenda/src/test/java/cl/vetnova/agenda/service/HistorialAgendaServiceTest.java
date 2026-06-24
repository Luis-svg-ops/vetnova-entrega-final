package cl.vetnova.agenda.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.agenda.model.HistorialAgenda;
import cl.vetnova.agenda.repository.HistorialAgendaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HistorialAgendaServiceTest {

    @Mock
    private HistorialAgendaRepository historialAgendaRepository;

    @InjectMocks
    private HistorialAgendaService historialAgendaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearYListarHistorial() {
        HistorialAgenda historial = new HistorialAgenda();
        historial.setCitaId(1L);
        when(historialAgendaRepository.save(any(HistorialAgenda.class))).thenAnswer(inv -> inv.getArgument(0));
        when(historialAgendaRepository.findAll()).thenReturn(List.of(historial));

        assertEquals(1L, historialAgendaService.crear(historial).getCitaId());
        assertEquals(1, historialAgendaService.listar().size());
    }

    @Test
    void testEliminarHistorialLlamaAlRepositorio() {
        historialAgendaService.eliminar(1L);

        verify(historialAgendaRepository).deleteById(1L);
    }
}
