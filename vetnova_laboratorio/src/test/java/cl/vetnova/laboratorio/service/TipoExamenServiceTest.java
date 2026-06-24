package cl.vetnova.laboratorio.service;

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

import cl.vetnova.laboratorio.dto.TipoExamenRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.TipoExamen;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;
import cl.vetnova.laboratorio.repository.TipoExamenRepository;

public class TipoExamenServiceTest {

    @Mock private TipoExamenRepository repository;
    @Mock private OrdenExamenRepository ordenRepository;
    @InjectMocks private TipoExamenService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TipoExamenRequest req(String nombre, Integer tiempo, Boolean requiere) {
        TipoExamenRequest r = new TipoExamenRequest();
        r.setNombre(nombre);
        r.setDescripcion("desc");
        r.setTiempoEstimadoHoras(tiempo);
        r.setRequiereMuestra(requiere);
        return r;
    }

    private TipoExamen tipo(Long id) {
        TipoExamen t = new TipoExamen();
        t.setId(id);
        t.setNombre("Hemograma");
        return t;
    }

    @Test
    void testCrearNombreNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 2, true)));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("  ", 2, true)));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearNombreDuplicado() {
        when(repository.existsByNombreIgnoreCase("Hemograma")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req("Hemograma", 2, true)));
        assertEquals("Ya existe un tipo de examen con ese nombre", ex.getMessage());
    }

    @Test
    void testCrearTiempoNull() {
        when(repository.existsByNombreIgnoreCase("Hemograma")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Hemograma", null, true)));
        assertEquals("El tiempo estimado es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTiempoNoPositivo() {
        when(repository.existsByNombreIgnoreCase("Hemograma")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Hemograma", 0, true)));
        assertEquals("El tiempo estimado debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearRequiereMuestraNull() {
        when(repository.existsByNombreIgnoreCase("Hemograma")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Hemograma", 2, null)));
        assertEquals("El campo requiereMuestra es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(repository.existsByNombreIgnoreCase("Hemograma")).thenReturn(false);
        when(repository.save(any(TipoExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        TipoExamen creado = service.crear(req("Hemograma", 2, true));
        assertEquals("Hemograma", creado.getNombre());
    }

    @Test
    void testActualizarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.actualizar(99L, req("Otro", 2, true)));
        assertEquals("Tipo de examen no encontrado", ex.getMessage());
    }

    @Test
    void testActualizarNombreDuplicado() {
        when(repository.findById(1L)).thenReturn(Optional.of(tipo(1L)));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Hemograma", 1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.actualizar(1L, req("Hemograma", 2, true)));
        assertEquals("Ya existe un tipo de examen con ese nombre", ex.getMessage());
    }

    @Test
    void testActualizarNombreVacio() {
        when(repository.findById(1L)).thenReturn(Optional.of(tipo(1L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.actualizar(1L, req("  ", 2, true)));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testActualizarTiempoNoPositivo() {
        when(repository.findById(1L)).thenReturn(Optional.of(tipo(1L)));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Otro", 1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.actualizar(1L, req("Otro", 0, true)));
        assertEquals("El tiempo estimado debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testActualizarCasoFelizCompleto() {
        when(repository.findById(1L)).thenReturn(Optional.of(tipo(1L)));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Otro", 1L)).thenReturn(false);
        when(repository.save(any(TipoExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        TipoExamenRequest r = req("Otro", 3, false);
        r.setInstrucciones("Ayuno de 12h");
        TipoExamen actualizado = service.actualizar(1L, r);
        assertEquals("Otro", actualizado.getNombre());
        assertEquals(3, actualizado.getTiempoEstimadoHoras());
        assertFalse(actualizado.getRequiereMuestra());
        assertEquals("Ayuno de 12h", actualizado.getInstrucciones());
    }

    @Test
    void testActualizarCasoFelizSinCambios() {
        when(repository.findById(1L)).thenReturn(Optional.of(tipo(1L)));
        when(repository.save(any(TipoExamen.class))).thenAnswer(inv -> inv.getArgument(0));
        TipoExamen actualizado = service.actualizar(1L, new TipoExamenRequest());
        assertEquals("Hemograma", actualizado.getNombre());
    }

    @Test
    void testEliminarConOrdenes() {
        when(repository.findById(1L)).thenReturn(Optional.of(tipo(1L)));
        when(ordenRepository.existsByTipoExamen_Id(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.eliminar(1L));
        assertEquals("No se puede eliminar un tipo de examen con órdenes asociadas", ex.getMessage());
    }

    @Test
    void testEliminarSinOrdenes() {
        TipoExamen t = tipo(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(t));
        when(ordenRepository.existsByTipoExamen_Id(1L)).thenReturn(false);
        service.eliminar(1L);
        verify(repository).delete(t);
    }

    @Test
    void testListar() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(tipo(1L)));
        assertEquals(1, service.listar().size());
    }
}
