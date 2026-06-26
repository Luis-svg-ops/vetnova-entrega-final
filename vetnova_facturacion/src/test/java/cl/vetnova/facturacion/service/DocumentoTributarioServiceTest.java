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

import cl.vetnova.facturacion.client.VentasClient;
import cl.vetnova.facturacion.dto.DocumentoTributarioRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.AnulacionDocumento;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.model.EnvioSII;
import cl.vetnova.facturacion.model.Folio;
import cl.vetnova.facturacion.repository.AnulacionDocumentoRepository;
import cl.vetnova.facturacion.repository.DocumentoTributarioRepository;
import cl.vetnova.facturacion.repository.EnvioSIIRepository;
import cl.vetnova.facturacion.repository.FolioRepository;

public class DocumentoTributarioServiceTest {

    @Mock private DocumentoTributarioRepository documentoRepository;
    @Mock private FolioRepository folioRepository;
    @Mock private AnulacionDocumentoRepository anulacionRepository;
    @Mock private EnvioSIIRepository envioRepository;
    @Mock private VentasClient ventasClient;
    @InjectMocks private DocumentoTributarioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private DocumentoTributarioRequest req() {
        DocumentoTributarioRequest r = new DocumentoTributarioRequest();
        r.setOrdenId(1L);
        r.setClienteId(1L);
        r.setTipo("BOLETA");
        r.setNeto(1000.0);
        r.setRutEmisor("11.111.111-1");
        r.setSucursal("CHILLAN");
        return r;
    }

    private DocumentoTributario doc(Long id, String estado) {
        DocumentoTributario d = new DocumentoTributario();
        d.setId(id);
        d.setEstadoSII(estado);
        return d;
    }

    private Folio folioDisponible() {
        Folio f = new Folio();
        f.setId(1L);
        f.setFolioActual(1);
        f.setFoliosRestantes(100);
        f.setActivo(true);
        return f;
    }

    private void ordenValida() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        when(ventasClient.ordenConfirmada(1L)).thenReturn(true);
        when(documentoRepository.existsByOrdenId(1L)).thenReturn(false);
    }

    // ---------- emitir ----------
    @Test
    void testEmitirOrdenIdNull() {
        DocumentoTributarioRequest r = req();
        r.setOrdenId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("El ordenId es obligatorio", ex.getMessage());
    }

    @Test
    void testEmitirOrdenInexistente() {
        when(ventasClient.ordenExiste(999L)).thenReturn(false);
        DocumentoTributarioRequest r = req();
        r.setOrdenId(999L);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.emitir(r));
        assertEquals("Orden no encontrada", ex.getMessage());
    }

    @Test
    void testEmitirOrdenNoConfirmada() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        when(ventasClient.ordenConfirmada(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(req()));
        assertEquals("Solo se puede emitir documento para una orden confirmada", ex.getMessage());
    }

    @Test
    void testEmitirOrdenYaTieneDocumento() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        when(ventasClient.ordenConfirmada(1L)).thenReturn(true);
        when(documentoRepository.existsByOrdenId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.emitir(req()));
        assertEquals("La orden ya tiene un documento tributario emitido", ex.getMessage());
    }

    @Test
    void testEmitirClienteIdNull() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setClienteId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    @Test
    void testEmitirTipoNull() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setTipo(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("El tipo de documento es obligatorio", ex.getMessage());
    }

    @Test
    void testEmitirTipoInvalido() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setTipo("RECIBO");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("Tipo no válido. Valores permitidos: BOLETA, FACTURA", ex.getMessage());
    }

    @Test
    void testEmitirFacturaSinRutReceptor() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setTipo("FACTURA");
        r.setRutReceptor(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("Se requiere RUT del receptor para emitir una factura", ex.getMessage());
    }

    @Test
    void testEmitirNetoNegativo() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setNeto(-500.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("El monto neto no puede ser negativo", ex.getMessage());
    }

    @Test
    void testEmitirTotalInconsistente() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setNeto(1000.0);
        r.setTotal(1100.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("El total debe ser igual a neto + IVA", ex.getMessage());
    }

    @Test
    void testEmitirRutEmisorNull() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setRutEmisor(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("El RUT del emisor es obligatorio", ex.getMessage());
    }

    @Test
    void testEmitirRutEmisorInvalido() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setRutEmisor("12345678-X");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("El RUT del emisor no es válido", ex.getMessage());
    }

    @Test
    void testEmitirSinFolioDisponible() {
        ordenValida();
        when(folioRepository.findFirstBySucursalAndTipoDocumentoAndActivoTrueAndFoliosRestantesGreaterThan("CHILLAN", "BOLETA", 0))
                .thenReturn(Optional.empty());
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(req()));
        assertEquals("No hay folios disponibles para emitir el documento", ex.getMessage());
    }

    @Test
    void testEmitirFacturaRutReceptorVacio() {
        ordenValida();
        DocumentoTributarioRequest r = req();
        r.setTipo("FACTURA");
        r.setRutReceptor("  ");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.emitir(r));
        assertEquals("Se requiere RUT del receptor para emitir una factura", ex.getMessage());
    }

    @Test
    void testEmitirCasoFeliz() {
        ordenValida();
        when(folioRepository.findFirstBySucursalAndTipoDocumentoAndActivoTrueAndFoliosRestantesGreaterThan("CHILLAN", "BOLETA", 0))
                .thenReturn(Optional.of(folioDisponible()));
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        DocumentoTributario creado = service.emitir(req());
        assertEquals("EMITIDO", creado.getEstadoSII());
        assertEquals(190.0, creado.getIva());
        assertEquals(1190.0, creado.getTotal());
        assertEquals("1", creado.getFolio());
    }

    @Test
    void testEmitirNetoNullUsaCero() {
        ordenValida();
        when(folioRepository.findFirstBySucursalAndTipoDocumentoAndActivoTrueAndFoliosRestantesGreaterThan("CHILLAN", "BOLETA", 0))
                .thenReturn(Optional.of(folioDisponible()));
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        DocumentoTributarioRequest r = req();
        r.setNeto(null);
        DocumentoTributario creado = service.emitir(r);
        assertEquals(0.0, creado.getNeto());
        assertEquals(0.0, creado.getIva());
    }

    @Test
    void testEmitirTotalConsistente() {
        ordenValida();
        when(folioRepository.findFirstBySucursalAndTipoDocumentoAndActivoTrueAndFoliosRestantesGreaterThan("CHILLAN", "BOLETA", 0))
                .thenReturn(Optional.of(folioDisponible()));
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        DocumentoTributarioRequest r = req();
        r.setTotal(1190.0);
        assertEquals(1190.0, service.emitir(r).getTotal());
    }

    @Test
    void testEmitirFacturaCasoFeliz() {
        ordenValida();
        when(folioRepository.findFirstBySucursalAndTipoDocumentoAndActivoTrueAndFoliosRestantesGreaterThan("CHILLAN", "FACTURA", 0))
                .thenReturn(Optional.of(folioDisponible()));
        when(folioRepository.save(any(Folio.class))).thenAnswer(inv -> inv.getArgument(0));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        DocumentoTributarioRequest r = req();
        r.setTipo("FACTURA");
        r.setRutReceptor("11.111.111-1");
        DocumentoTributario creado = service.emitir(r);
        assertEquals("FACTURA", creado.getTipo());
        assertEquals("11.111.111-1", creado.getRutReceptor());
    }

    // ---------- anular ----------
    @Test
    void testAnularInexistente() {
        when(documentoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.anular(99L, "motivo"));
    }

    @Test
    void testAnularYaAnulado() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "ANULADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.anular(1L, "motivo"));
        assertEquals("El documento ya está anulado", ex.getMessage());
    }

    @Test
    void testAnularSinMotivo() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.anular(1L, null));
        assertEquals("El motivo de anulación es obligatorio", ex.getMessage());
    }

    @Test
    void testAnularMotivoVacio() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.anular(1L, "  "));
        assertEquals("El motivo de anulación es obligatorio", ex.getMessage());
    }

    @Test
    void testAnularCasoFeliz() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        DocumentoTributario anulado = service.anular(1L, "Error en monto");
        assertEquals("ANULADO", anulado.getEstadoSII());
        verify(anulacionRepository).save(any(AnulacionDocumento.class));
    }

    // ---------- enviarAlSII ----------
    @Test
    void testEnviarAlSIIInexistente() {
        when(documentoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.enviarAlSII(99L));
    }

    @Test
    void testEnviarAlSIIAnulado() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "ANULADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.enviarAlSII(1L));
        assertEquals("No se puede enviar al SII un documento anulado", ex.getMessage());
    }

    @Test
    void testEnviarAlSIICasoFeliz() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        service.enviarAlSII(1L);
        verify(envioRepository).save(any(EnvioSII.class));
    }

    @Test
    void testListar() {
        when(documentoRepository.findAll()).thenReturn(List.of(doc(1L, "EMITIDO")));
        assertEquals(1, service.listar().size());
    }
}
