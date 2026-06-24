package cl.vetnova.notificaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ConflictException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.PlantillaMensaje;
import cl.vetnova.notificaciones.repository.PlantillaMensajeRepository;

public class PlantillaMensajeServiceTest {

    @Mock private PlantillaMensajeRepository plantillaRepository;
    @InjectMocks private PlantillaMensajeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PlantillaMensaje req(String nombre, String tipo, String contenido) {
        PlantillaMensaje p = new PlantillaMensaje();
        p.setNombre(nombre);
        p.setTipo(tipo);
        p.setContenido(contenido);
        return p;
    }

    private PlantillaMensaje plantilla(Long id, String contenido, Boolean activa) {
        PlantillaMensaje p = new PlantillaMensaje();
        p.setId(id);
        p.setContenido(contenido);
        p.setActiva(activa);
        return p;
    }

    @Test
    void testCrearNombreNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, "EMAIL", "Hola")));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("  ", "EMAIL", "Hola")));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearNombreDuplicado() {
        when(plantillaRepository.existsByNombreIgnoreCase("Confirmacion Cita")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req("Confirmacion Cita", "SMS", "Hola")));
        assertEquals("Ya existe una plantilla con ese nombre", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        when(plantillaRepository.existsByNombreIgnoreCase("Test")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Test", null, "Hola")));
        assertEquals("El tipo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        when(plantillaRepository.existsByNombreIgnoreCase("Test")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Test", "FAX", "Hola")));
        assertEquals("Tipo no válido. Valores permitidos: EMAIL, SMS, PUSH, SISTEMA", ex.getMessage());
    }

    @Test
    void testCrearContenidoNull() {
        when(plantillaRepository.existsByNombreIgnoreCase("Test")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Test", "EMAIL", null)));
        assertEquals("El contenido es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearContenidoVacio() {
        when(plantillaRepository.existsByNombreIgnoreCase("Test")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Test", "EMAIL", "  ")));
        assertEquals("El contenido no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearVariableInvalida() {
        when(plantillaRepository.existsByNombreIgnoreCase("Test")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Test", "EMAIL", "Hola {{nombreInvalido123!}}")));
        assertEquals("La plantilla contiene variables con formato inválido", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(plantillaRepository.existsByNombreIgnoreCase("Recordatorio")).thenReturn(false);
        when(plantillaRepository.save(any(PlantillaMensaje.class))).thenAnswer(inv -> inv.getArgument(0));
        PlantillaMensaje p = service.crear(req("Recordatorio", "EMAIL", "Hola {{nombre}}, su cita el {{fecha}}"));
        assertTrue(p.getActiva());
    }

    @Test
    void testRenderizarInexistente() {
        when(plantillaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.renderizar(99L, Map.of()));
    }

    @Test
    void testRenderizarInactiva() {
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(plantilla(1L, "Hola {{nombre}}", false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.renderizar(1L, Map.of("nombre", "Juan")));
        assertEquals("La plantilla está inactiva y no puede usarse", ex.getMessage());
    }

    @Test
    void testRenderizarVariableNoProvista() {
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(plantilla(1L, "Hola {{nombre}}, su cita es el {{fecha}}", true)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.renderizar(1L, Map.of("nombre", "Juan")));
        assertEquals("Falta el valor para la variable: fecha", ex.getMessage());
    }

    @Test
    void testRenderizarValoresNull() {
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(plantilla(1L, "Hola {{nombre}}", true)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.renderizar(1L, null));
        assertEquals("Falta el valor para la variable: nombre", ex.getMessage());
    }

    @Test
    void testRenderizarCasoFeliz() {
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(plantilla(1L, "Hola {{nombre}}, su cita es el {{fecha}}", true)));
        String resultado = service.renderizar(1L, Map.of("nombre", "Juan", "fecha", "2025-07-01"));
        assertEquals("Hola Juan, su cita es el 2025-07-01", resultado);
    }

    @Test
    void testActualizarCasoFeliz() {
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(plantilla(1L, "viejo", true)));
        when(plantillaRepository.save(any(PlantillaMensaje.class))).thenAnswer(inv -> inv.getArgument(0));
        PlantillaMensaje req = new PlantillaMensaje();
        req.setContenido("Estimado {{nombre}}");
        PlantillaMensaje p = service.actualizar(1L, req);
        assertEquals("Estimado {{nombre}}", p.getContenido());
    }

    @Test
    void testActualizarSinContenido() {
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(plantilla(1L, "viejo", true)));
        when(plantillaRepository.save(any(PlantillaMensaje.class))).thenAnswer(inv -> inv.getArgument(0));
        PlantillaMensaje p = service.actualizar(1L, new PlantillaMensaje());
        assertEquals("viejo", p.getContenido());
    }

    @Test
    void testEliminar() {
        PlantillaMensaje p = plantilla(1L, "Hola", true);
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(p));
        service.eliminar(1L);
        verify(plantillaRepository).delete(p);
    }
}
