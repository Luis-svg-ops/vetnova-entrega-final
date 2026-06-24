package cl.vetnova.facturacion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.facturacion.dto.AnulacionRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.AnulacionDocumento;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.repository.AnulacionDocumentoRepository;
import cl.vetnova.facturacion.repository.DocumentoTributarioRepository;

public class AnulacionDocumentoServiceTest {

    @Mock private AnulacionDocumentoRepository anulacionRepository;
    @Mock private DocumentoTributarioRepository documentoRepository;
    @InjectMocks private AnulacionDocumentoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private AnulacionRequest req(Long documentoId, Long adminId, String motivo) {
        AnulacionRequest r = new AnulacionRequest();
        r.setDocumentoId(documentoId);
        r.setAdministradorId(adminId);
        r.setMotivo(motivo);
        return r;
    }

    private DocumentoTributario doc(Long id, String estado) {
        DocumentoTributario d = new DocumentoTributario();
        d.setId(id);
        d.setEstadoSII(estado);
        return d;
    }

    private AnulacionDocumento anulacion(Long id, Long documentoId) {
        AnulacionDocumento a = new AnulacionDocumento();
        a.setId(id);
        a.setDocumentoId(documentoId);
        return a;
    }

    @Test
    void testRegistrarDocumentoIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(null, 1L, "motivo")));
        assertEquals("El documentoId es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarDocumentoInexistente() {
        when(documentoRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrar(req(999L, 1L, "motivo")));
        assertEquals("Documento tributario no encontrado", ex.getMessage());
    }

    @Test
    void testRegistrarDocumentoYaAnulado() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "ANULADO")));
        ConflictException ex = assertThrows(ConflictException.class, () -> service.registrar(req(1L, 1L, "motivo")));
        assertEquals("El documento ya está anulado", ex.getMessage());
    }

    @Test
    void testRegistrarDocumentoRechazado() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "RECHAZADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 1L, "motivo")));
        assertEquals("No se puede anular un documento rechazado por el SII", ex.getMessage());
    }

    @Test
    void testRegistrarAdministradorIdNull() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, null, "motivo")));
        assertEquals("El administradorId es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarMotivoNull() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 1L, null)));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarMotivoVacio() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 1L, "  ")));
        assertEquals("El motivo no puede estar vacío", ex.getMessage());
    }

    @Test
    void testRegistrarCasoFeliz() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        when(anulacionRepository.save(any(AnulacionDocumento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        AnulacionDocumento a = service.registrar(req(1L, 1L, "Monto incorrecto"));
        assertEquals("EMITIDO", a.getEstadoSII());
        assertNotNull(a.getFechaAnulacion());
    }

    @Test
    void testNotificarSIIInexistente() {
        when(anulacionRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.notificarSII(99L));
        assertEquals("Anulación no encontrada", ex.getMessage());
    }

    @Test
    void testNotificarSIIDocumentoNoAnulado() {
        when(anulacionRepository.findById(1L)).thenReturn(Optional.of(anulacion(1L, 1L)));
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.notificarSII(1L));
        assertEquals("El documento debe estar anulado antes de notificar al SII", ex.getMessage());
    }

    @Test
    void testNotificarSIIDocumentoInexistente() {
        when(anulacionRepository.findById(1L)).thenReturn(Optional.of(anulacion(1L, 1L)));
        when(documentoRepository.findById(1L)).thenReturn(Optional.empty());
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.notificarSII(1L));
        assertEquals("El documento debe estar anulado antes de notificar al SII", ex.getMessage());
    }

    @Test
    void testNotificarSIICasoFeliz() {
        when(anulacionRepository.findById(1L)).thenReturn(Optional.of(anulacion(1L, 1L)));
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "ANULADO")));
        when(anulacionRepository.save(any(AnulacionDocumento.class))).thenAnswer(inv -> inv.getArgument(0));
        AnulacionDocumento a = service.notificarSII(1L);
        assertEquals("NOTIFICADO", a.getEstadoSII());
    }
}
