package cl.vetnova.facturacion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.facturacion.dto.FolioRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.Folio;
import cl.vetnova.facturacion.repository.FolioRepository;

public class FolioServiceTest {

    @Mock private FolioRepository folioRepository;
    @InjectMocks private FolioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private FolioRequest req(String sucursal, String tipo, Integer desde, Integer hasta) {
        FolioRequest r = new FolioRequest();
        r.setSucursal(sucursal);
        r.setTipoDocumento(tipo);
        r.setFolioDesde(desde);
        r.setFolioHasta(hasta);
        return r;
    }

    private Folio folio(Long id, Integer actual, Integer restantes, Boolean activo, Integer umbral) {
        Folio f = new Folio();
        f.setId(id);
        f.setFolioDesde(1);
        f.setFolioHasta(100);
        f.setFolioActual(actual);
        f.setFoliosRestantes(restantes);
        f.setActivo(activo);
        f.setUmbral(umbral);
        return f;
    }

    @Test
    void testCrearSucursalNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, "BOLETA", 1, 100)));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("CHILLAN", null, 1, 100)));
        assertEquals("El tipo de documento es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("CHILLAN", "RECIBO", 1, 100)));
        assertEquals("Tipo no válido. Valores permitidos: BOLETA, FACTURA", ex.getMessage());
    }

    @Test
    void testCrearFolioDesdeNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("CHILLAN", "BOLETA", null, 100)));
        assertEquals("El folio inicial es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFolioDesdeNoPositivo() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("CHILLAN", "BOLETA", 0, 100)));
        assertEquals("El folio inicial debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearFolioHastaNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("CHILLAN", "BOLETA", 1, null)));
        assertEquals("El folio final es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFolioHastaMenorQueDesde() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("CHILLAN", "BOLETA", 100, 50)));
        assertEquals("El folio final debe ser mayor o igual al folio inicial", ex.getMessage());
    }

    @Test
    void testCrearRangoSuperpuesto() {
        Folio existente = folio(1L, 1, 100, true, 10);
        existente.setFolioDesde(1);
        existente.setFolioHasta(100);
        when(folioRepository.findBySucursalAndTipoDocumento("CHILLAN", "BOLETA")).thenReturn(List.of(existente));
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req("CHILLAN", "BOLETA", 50, 150)));
        assertEquals("El rango de folios se superpone con uno existente", ex.getMessage());
    }

    @Test
    void testCrearRangoNoSuperpuesto() {
        Folio antes = folio(1L, 1, 40, true, 10);
        antes.setFolioDesde(1);
        antes.setFolioHasta(40);
        Folio despues = folio(2L, 200, 300, true, 10);
        despues.setFolioDesde(200);
        despues.setFolioHasta(300);
        when(folioRepository.findBySucursalAndTipoDocumento("CHILLAN", "BOLETA")).thenReturn(List.of(antes, despues));
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        Folio creado = service.crear(req("CHILLAN", "BOLETA", 50, 100));
        assertEquals(51, creado.getFoliosRestantes());
    }

    @Test
    void testCrearCasoFeliz() {
        when(folioRepository.findBySucursalAndTipoDocumento("CHILLAN", "BOLETA")).thenReturn(List.of());
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        Folio creado = service.crear(req("CHILLAN", "BOLETA", 1, 100));
        assertEquals(1, creado.getFolioActual());
        assertEquals(100, creado.getFoliosRestantes());
        assertTrue(creado.getActivo());
    }

    @Test
    void testCrearRangoUnitario() {
        when(folioRepository.findBySucursalAndTipoDocumento("CHILLAN", "BOLETA")).thenReturn(List.of());
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        Folio creado = service.crear(req("CHILLAN", "BOLETA", 100, 100));
        assertEquals(1, creado.getFoliosRestantes());
    }

    @Test
    void testGetSiguienteFolioInexistente() {
        when(folioRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.getSiguienteFolio(99L));
        assertEquals("Folio no encontrado", ex.getMessage());
    }

    @Test
    void testGetSiguienteFolioAgotado() {
        when(folioRepository.findById(1L)).thenReturn(Optional.of(folio(1L, 100, 0, true, 10)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.getSiguienteFolio(1L));
        assertEquals("No hay folios disponibles", ex.getMessage());
    }

    @Test
    void testGetSiguienteFolioInactivo() {
        when(folioRepository.findById(1L)).thenReturn(Optional.of(folio(1L, 5, 96, false, 10)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.getSiguienteFolio(1L));
        assertEquals("El rango de folios está inactivo", ex.getMessage());
    }

    @Test
    void testGetSiguienteFolioCasoFeliz() {
        when(folioRepository.findById(1L)).thenReturn(Optional.of(folio(1L, 5, 96, true, 10)));
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        Integer siguiente = service.getSiguienteFolio(1L);
        assertEquals(5, siguiente);
    }

    @Test
    void testRequiereAlertaPorEncimaDelUmbral() {
        when(folioRepository.findById(1L)).thenReturn(Optional.of(folio(1L, 50, 50, true, 10)));
        assertFalse(service.requiereAlerta(1L));
    }

    @Test
    void testRequiereAlertaBajoUmbral() {
        when(folioRepository.findById(1L)).thenReturn(Optional.of(folio(1L, 95, 5, true, 10)));
        assertTrue(service.requiereAlerta(1L));
    }

    @Test
    void testListar() {
        when(folioRepository.findAll()).thenReturn(List.of(folio(1L, 1, 100, true, 10)));
        assertEquals(1, service.listar().size());
    }

    @Test
    void testBuscar() {
        when(folioRepository.findById(eq(1L))).thenReturn(Optional.of(folio(1L, 1, 100, true, 10)));
        assertEquals(1L, service.buscar(1L).getId());
    }
}
