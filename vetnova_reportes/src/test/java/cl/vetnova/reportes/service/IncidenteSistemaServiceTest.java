package cl.vetnova.reportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.IncidenteSistema;
import cl.vetnova.reportes.repository.IncidenteSistemaRepository;

public class IncidenteSistemaServiceTest {

    @Mock private IncidenteSistemaRepository incidenteRepository;
    @InjectMocks private IncidenteSistemaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private IncidenteSistema req(String micro, String tipo, String severidad, String descripcion) {
        IncidenteSistema i = new IncidenteSistema();
        i.setMicroservicio(micro);
        i.setTipo(tipo);
        i.setSeveridad(severidad);
        i.setDescripcion(descripcion);
        return i;
    }

    private IncidenteSistema incidente(Long id, String estado, Boolean notificado) {
        IncidenteSistema i = new IncidenteSistema();
        i.setId(id);
        i.setEstado(estado);
        i.setNotificado(notificado);
        return i;
    }

    @Test
    void testRegistrarMicroservicioNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(null, "DOWN", "CRITICA", "x")));
        assertEquals("El microservicio es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarMicroservicioInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS99", "DOWN", "CRITICA", "x")));
        assertEquals("Microservicio no válido. Debe ser uno de los 12 microservicios del sistema", ex.getMessage());
    }

    @Test
    void testRegistrarTipoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS1", null, "CRITICA", "x")));
        assertEquals("El tipo de incidente es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarTipoVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS1", "  ", "CRITICA", "x")));
        assertEquals("El tipo de incidente no puede estar vacío", ex.getMessage());
    }

    @Test
    void testRegistrarSeveridadNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS1", "DOWN", null, "x")));
        assertEquals("La severidad es obligatoria", ex.getMessage());
    }

    @Test
    void testRegistrarSeveridadInvalida() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS1", "DOWN", "EXTREMA", "x")));
        assertEquals("Severidad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA", ex.getMessage());
    }

    @Test
    void testRegistrarDescripcionNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS1", "DOWN", "CRITICA", null)));
        assertEquals("La descripción es obligatoria", ex.getMessage());
    }

    @Test
    void testRegistrarDuplicadoAbierto() {
        when(incidenteRepository.existsByMicroservicioAndTipoAndEstado("MS1", "DOWN", "ABIERTO")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.registrar(req("MS1", "DOWN", "CRITICA", "x")));
        assertEquals("Ya existe un incidente abierto para ese microservicio y tipo", ex.getMessage());
    }

    @Test
    void testRegistrarCasoFeliz() {
        when(incidenteRepository.existsByMicroservicioAndTipoAndEstado("MS1", "DOWN", "ABIERTO")).thenReturn(false);
        when(incidenteRepository.save(any(IncidenteSistema.class))).thenAnswer(inv -> inv.getArgument(0));
        IncidenteSistema i = service.registrar(req("MS1", "DOWN", "CRITICA", "Servicio caído"));
        assertEquals("ABIERTO", i.getEstado());
        assertFalse(i.getNotificado());
        assertNotNull(i.getFechaDeteccion());
    }

    @Test
    void testNotificarInexistente() {
        when(incidenteRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.notificarAdministrador(99L));
        assertEquals("Incidente no encontrado", ex.getMessage());
    }

    @Test
    void testNotificarYaNotificado() {
        when(incidenteRepository.findById(1L)).thenReturn(Optional.of(incidente(1L, "ABIERTO", true)));
        IncidenteSistema i = service.notificarAdministrador(1L);
        assertTrue(i.getNotificado());
        verify(incidenteRepository, never()).save(any());
    }

    @Test
    void testNotificarCasoFeliz() {
        when(incidenteRepository.findById(1L)).thenReturn(Optional.of(incidente(1L, "ABIERTO", false)));
        when(incidenteRepository.save(any(IncidenteSistema.class))).thenAnswer(inv -> inv.getArgument(0));
        IncidenteSistema i = service.notificarAdministrador(1L);
        assertTrue(i.getNotificado());
    }

    @Test
    void testResolverYaResuelto() {
        when(incidenteRepository.findById(1L)).thenReturn(Optional.of(incidente(1L, "RESUELTO", false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.resolver(1L));
        assertEquals("El incidente ya está resuelto", ex.getMessage());
    }

    @Test
    void testResolverCasoFeliz() {
        when(incidenteRepository.findById(1L)).thenReturn(Optional.of(incidente(1L, "ABIERTO", false)));
        when(incidenteRepository.save(any(IncidenteSistema.class))).thenAnswer(inv -> inv.getArgument(0));
        IncidenteSistema i = service.resolver(1L);
        assertEquals("RESUELTO", i.getEstado());
        assertNotNull(i.getFechaResolucion());
    }
}
