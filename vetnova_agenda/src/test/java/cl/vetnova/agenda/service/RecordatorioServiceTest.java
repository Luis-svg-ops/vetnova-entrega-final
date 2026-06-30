package cl.vetnova.agenda.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.agenda.dto.RecordatorioRequest;
import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.Cita;
import cl.vetnova.agenda.model.Recordatorio;
import cl.vetnova.agenda.repository.CitaRepository;
import cl.vetnova.agenda.repository.RecordatorioRepository;

public class RecordatorioServiceTest {

    @Mock
    private RecordatorioRepository recordatorioRepository;
    @Mock
    private CitaRepository citaRepository;
    @InjectMocks
    private RecordatorioService recordatorioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Cita cita(LocalDateTime fechaHora) {
        Cita c = new Cita();
        c.setId(1L);
        c.setVeterinarioId(4L);
        c.setSucursal("SANTIAGO");
        c.setFechaHora(fechaHora);
        return c;
    }

    private Recordatorio recordatorio(Boolean enviado) {
        Recordatorio r = new Recordatorio();
        r.setId(1L);
        r.setCitaId(1L);
        r.setTipo("EMAIL");
        r.setEnviado(enviado);
        return r;
    }

    // ---- crear ----

    @Test
    void testCrearCitaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recordatorioService.crear(new RecordatorioRequest(null, "EMAIL")));
        assertEquals("El citaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearCitaInexistente() {
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> recordatorioService.crear(new RecordatorioRequest(999L, "EMAIL")));
        assertEquals("Cita no encontrada", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita(LocalDateTime.of(2030, 7, 1, 10, 0))));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recordatorioService.crear(new RecordatorioRequest(1L, null)));
        assertEquals("El tipo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita(LocalDateTime.of(2030, 7, 1, 10, 0))));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recordatorioService.crear(new RecordatorioRequest(1L, "CARTA")));
        assertEquals("Tipo no válido. Valores: EMAIL, SMS, PUSH", ex.getMessage());
    }

    @Test
    void testCrearDuplicado() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita(LocalDateTime.of(2030, 7, 1, 10, 0))));
        when(recordatorioRepository.existsByCitaIdAndTipo(1L, "EMAIL")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> recordatorioService.crear(new RecordatorioRequest(1L, "EMAIL")));
        assertEquals("Ya existe recordatorio de ese tipo para esta cita", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizCitaLejana() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita(LocalDateTime.of(2030, 7, 1, 10, 0))));
        when(recordatorioRepository.existsByCitaIdAndTipo(1L, "EMAIL")).thenReturn(false);
        when(recordatorioRepository.save(any(Recordatorio.class))).thenAnswer(inv -> inv.getArgument(0));
        Recordatorio creado = recordatorioService.crear(new RecordatorioRequest(1L, "EMAIL"));
        assertEquals(false, creado.getEnviado());
        assertEquals(LocalDateTime.of(2030, 6, 30, 10, 0), creado.getFechaEnvio());
        assertTrue(creado.getMensaje().contains("SANTIAGO"));
    }

    @Test
    void testCrearCasoFelizCitaProximaEnvioInmediato() {
        LocalDateTime cercana = LocalDateTime.now().plusHours(1);
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita(cercana)));
        when(recordatorioRepository.existsByCitaIdAndTipo(1L, "EMAIL")).thenReturn(false);
        when(recordatorioRepository.save(any(Recordatorio.class))).thenAnswer(inv -> inv.getArgument(0));
        Recordatorio creado = recordatorioService.crear(new RecordatorioRequest(1L, "EMAIL"));
        assertTrue(creado.getFechaEnvio().isAfter(cercana.minusHours(24)));
    }

    // ---- reenviar ----

    @Test
    void testReenviar() {
        when(recordatorioRepository.findById(1L)).thenReturn(Optional.of(recordatorio(false)));
        when(recordatorioRepository.save(any(Recordatorio.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(true, recordatorioService.reenviar(1L).getEnviado());
    }

    @Test
    void testReenviarInexistenteLanzaNotFound() {
        when(recordatorioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> recordatorioService.reenviar(99L));
    }

    // ---- generarParaCita (CA-REC-09, 10) ----

    @Test
    void testGenerarParaCitaCrea() {
        when(recordatorioRepository.existsByCitaIdAndTipo(1L, "EMAIL")).thenReturn(false);
        recordatorioService.generarParaCita(cita(LocalDateTime.of(2030, 7, 1, 10, 0)));
        verify(recordatorioRepository).save(any(Recordatorio.class));
    }

    @Test
    void testGenerarParaCitaNoDuplica() {
        when(recordatorioRepository.existsByCitaIdAndTipo(1L, "EMAIL")).thenReturn(true);
        recordatorioService.generarParaCita(cita(LocalDateTime.of(2030, 7, 1, 10, 0)));
        verify(recordatorioRepository, never()).save(any(Recordatorio.class));
    }

    // ---- cancelarPorCita (CA-REC-11) ----

    @Test
    void testCancelarPorCitaMarcaNoEnviados() {
        Recordatorio pendiente = recordatorio(false);
        when(recordatorioRepository.findByCitaId(1L)).thenReturn(List.of(pendiente));
        when(recordatorioRepository.save(any(Recordatorio.class))).thenAnswer(inv -> inv.getArgument(0));
        recordatorioService.cancelarPorCita(1L);
        assertEquals(true, pendiente.getCancelado());
    }

    @Test
    void testCancelarPorCitaIgnoraEnviados() {
        when(recordatorioRepository.findByCitaId(1L)).thenReturn(List.of(recordatorio(true)));
        recordatorioService.cancelarPorCita(1L);
        verify(recordatorioRepository, never()).save(any(Recordatorio.class));
    }

    // ---- listar / obtener ----

    @Test
    void testListar() {
        when(recordatorioRepository.findAll()).thenReturn(List.of(new Recordatorio()));
        assertEquals(1, recordatorioService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(recordatorioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> recordatorioService.obtenerPorId(99L));
    }

    @Test
    void testObtenerPorIdExistenteDevuelveElRecordatorio() {
        when(recordatorioRepository.findById(1L)).thenReturn(Optional.of(recordatorio(false)));
        assertEquals(1L, recordatorioService.obtenerPorId(1L).getCitaId());
    }

    @Test
    void testReenviarYaEnviadoSigueMarcandoEnviado() {
        when(recordatorioRepository.findById(1L)).thenReturn(Optional.of(recordatorio(true)));
        when(recordatorioRepository.save(any(Recordatorio.class))).thenAnswer(inv -> inv.getArgument(0));
        assertTrue(recordatorioService.reenviar(1L).getEnviado());
    }
}
