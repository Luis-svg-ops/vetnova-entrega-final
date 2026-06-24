package cl.vetnova.envio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ConflictException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.RutaDespacho;
import cl.vetnova.envio.repository.RutaDespachoRepository;

public class RutaDespachoServiceTest {

    @Mock
    private RutaDespachoRepository rutaDespachoRepository;
    @InjectMocks
    private RutaDespachoService rutaDespachoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private RutaDespacho ruta(String origen, String destino, double distancia, int tiempo) {
        RutaDespacho r = new RutaDespacho();
        r.setSucursalOrigen(origen);
        r.setSucursalDestino(destino);
        r.setDistanciaKm(distancia);
        r.setTiempoEstimadoMin(tiempo);
        return r;
    }

    // ---- crear ----

    @Test
    void testCrearOrigenNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rutaDespachoService.crear(ruta(null, "2", 10, 30)));
        assertEquals("La sucursal de origen es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearOrigenInexistente() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> rutaDespachoService.crear(ruta("999", "2", 10, 30)));
        assertEquals("Sucursal de origen no encontrada", ex.getMessage());
    }

    @Test
    void testCrearDestinoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rutaDespachoService.crear(ruta("1", null, 10, 30)));
        assertEquals("La sucursal de destino es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearDestinoInexistente() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> rutaDespachoService.crear(ruta("1", "999", 10, 30)));
        assertEquals("Sucursal de destino no encontrada", ex.getMessage());
    }

    @Test
    void testCrearOrigenIgualDestino() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rutaDespachoService.crear(ruta("1", "1", 10, 30)));
        assertEquals("La sucursal de destino debe ser distinta a la de origen", ex.getMessage());
    }

    @Test
    void testCrearRutaDuplicada() {
        when(rutaDespachoRepository.existsBySucursalOrigenAndSucursalDestino("1", "2")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> rutaDespachoService.crear(ruta("1", "2", 10, 30)));
        assertEquals("Ya existe una ruta entre estas sucursales", ex.getMessage());
    }

    @Test
    void testCrearDistanciaNegativa() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rutaDespachoService.crear(ruta("1", "2", -5, 30)));
        assertEquals("La distancia no puede ser negativa", ex.getMessage());
    }

    @Test
    void testCrearTiempoNegativo() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rutaDespachoService.crear(ruta("1", "2", 10, -15)));
        assertEquals("El tiempo estimado no puede ser negativo", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizConBordesEnCero() {
        when(rutaDespachoRepository.existsBySucursalOrigenAndSucursalDestino("1", "2")).thenReturn(false);
        when(rutaDespachoRepository.save(any(RutaDespacho.class))).thenAnswer(inv -> inv.getArgument(0));
        RutaDespacho creada = rutaDespachoService.crear(ruta("1", "2", 0, 0));
        assertEquals(true, creada.getActiva());
    }

    // ---- optimizar (CA-RUT-13/14) ----

    @Test
    void testOptimizarSinRutasActivas() {
        when(rutaDespachoRepository.findBySucursalOrigenAndSucursalDestinoAndActivaTrue("1", "3"))
                .thenReturn(List.of());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> rutaDespachoService.optimizar("1", "3"));
        assertEquals("No se encontró ruta activa entre las sucursales indicadas", ex.getMessage());
    }

    @Test
    void testOptimizarDevuelveLaDeMenorTiempo() {
        when(rutaDespachoRepository.findBySucursalOrigenAndSucursalDestinoAndActivaTrue("1", "2"))
                .thenReturn(List.of(ruta("1", "2", 10, 30), ruta("1", "2", 12, 20)));
        assertEquals(20, rutaDespachoService.optimizar("1", "2").getTiempoEstimadoMin());
    }

    // ---- calcularTiempoEstimado (CA-RUT-15) ----

    @Test
    void testCalcularTiempoEstimado() {
        RutaDespacho r = ruta("1", "2", 10, 45);
        when(rutaDespachoRepository.findById(1L)).thenReturn(Optional.of(r));
        assertEquals(45, rutaDespachoService.calcularTiempoEstimado(1L));
    }

    // ---- CRUD ----

    @Test
    void testListar() {
        when(rutaDespachoRepository.findAll()).thenReturn(List.of(new RutaDespacho()));
        assertEquals(1, rutaDespachoService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(rutaDespachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> rutaDespachoService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(rutaDespachoRepository.findById(1L)).thenReturn(Optional.of(new RutaDespacho()));
        when(rutaDespachoRepository.save(any(RutaDespacho.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(rutaDespachoService.actualizar(1L, ruta("1", "2", 10, 30)));
    }

    @Test
    void testEliminarExistente() {
        when(rutaDespachoRepository.existsById(1L)).thenReturn(true);
        rutaDespachoService.eliminar(1L);
        verify(rutaDespachoRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(rutaDespachoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> rutaDespachoService.eliminar(99L));
        verify(rutaDespachoRepository, never()).deleteById(anyLong());
    }
}
