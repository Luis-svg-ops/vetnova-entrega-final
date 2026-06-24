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

import cl.vetnova.facturacion.dto.EnvioSIIRequest;
import cl.vetnova.facturacion.dto.ProcesarRespuestaSiiRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.model.EnvioSII;
import cl.vetnova.facturacion.repository.DocumentoTributarioRepository;
import cl.vetnova.facturacion.repository.EnvioSIIRepository;

public class EnvioSIIServiceTest {

    @Mock private EnvioSIIRepository envioRepository;
    @Mock private DocumentoTributarioRepository documentoRepository;
    @InjectMocks private EnvioSIIService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private EnvioSIIRequest req(Long documentoId) {
        EnvioSIIRequest r = new EnvioSIIRequest();
        r.setDocumentoId(documentoId);
        return r;
    }

    private DocumentoTributario doc(Long id, String estado) {
        DocumentoTributario d = new DocumentoTributario();
        d.setId(id);
        d.setEstadoSII(estado);
        return d;
    }

    private EnvioSII envio(Long id, String estado, Boolean reintentado) {
        EnvioSII e = new EnvioSII();
        e.setId(id);
        e.setDocumentoId(1L);
        e.setEstado(estado);
        e.setReintentado(reintentado);
        return e;
    }

    private ProcesarRespuestaSiiRequest respuesta(String codigo) {
        ProcesarRespuestaSiiRequest r = new ProcesarRespuestaSiiRequest();
        r.setCodigo(codigo);
        r.setDescripcion("desc");
        return r;
    }

    // ---------- enviar ----------
    @Test
    void testEnviarDocumentoIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.enviar(req(null)));
        assertEquals("El documentoId es obligatorio", ex.getMessage());
    }

    @Test
    void testEnviarDocumentoInexistente() {
        when(documentoRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.enviar(req(999L)));
        assertEquals("Documento tributario no encontrado", ex.getMessage());
    }

    @Test
    void testEnviarDocumentoAnulado() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "ANULADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.enviar(req(1L)));
        assertEquals("No se puede enviar al SII un documento anulado", ex.getMessage());
    }

    @Test
    void testEnviarDuplicadoAceptado() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        when(envioRepository.existsByDocumentoIdAndEstado(1L, "ACEPTADO")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.enviar(req(1L)));
        assertEquals("El documento ya fue enviado y aceptado por el SII", ex.getMessage());
    }

    @Test
    void testEnviarCasoFeliz() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        when(envioRepository.existsByDocumentoIdAndEstado(1L, "ACEPTADO")).thenReturn(false);
        when(envioRepository.save(any(EnvioSII.class))).thenAnswer(inv -> inv.getArgument(0));
        EnvioSII e = service.enviar(req(1L));
        assertEquals("ENVIADO", e.getEstado());
        assertFalse(e.getReintentado());
    }

    // ---------- procesarRespuesta ----------
    @Test
    void testProcesarRespuestaEnvioInexistente() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.procesarRespuesta(99L, respuesta("ACEPTADO")));
        assertEquals("Envío SII no encontrado", ex.getMessage());
    }

    @Test
    void testProcesarRespuestaRequestNull() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio(1L, "ENVIADO", false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.procesarRespuesta(1L, null));
        assertEquals("La respuesta del SII no puede ser nula", ex.getMessage());
    }

    @Test
    void testProcesarRespuestaNula() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio(1L, "ENVIADO", false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.procesarRespuesta(1L, respuesta(null)));
        assertEquals("La respuesta del SII no puede ser nula", ex.getMessage());
    }

    @Test
    void testProcesarRespuestaAceptado() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio(1L, "ENVIADO", false)));
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(envioRepository.save(any(EnvioSII.class))).thenAnswer(inv -> inv.getArgument(0));
        EnvioSII e = service.procesarRespuesta(1L, respuesta("ACEPTADO"));
        assertEquals("ACEPTADO", e.getEstado());
    }

    @Test
    void testProcesarRespuestaRechazado() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio(1L, "ENVIADO", false)));
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(doc(1L, "EMITIDO")));
        when(documentoRepository.save(any(DocumentoTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(envioRepository.save(any(EnvioSII.class))).thenAnswer(inv -> inv.getArgument(0));
        EnvioSII e = service.procesarRespuesta(1L, respuesta("RECHAZADO"));
        assertEquals("RECHAZADO", e.getEstado());
    }

    // ---------- reintentar ----------
    @Test
    void testReintentarYaAceptado() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio(1L, "ACEPTADO", false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.reintentar(1L));
        assertEquals("No se puede reintentar un envío ya aceptado", ex.getMessage());
    }

    @Test
    void testReintentarLimiteAlcanzado() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio(1L, "ERROR", true)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.reintentar(1L));
        assertEquals("Se alcanzó el límite de reintentos para este envío", ex.getMessage());
    }

    @Test
    void testReintentarCasoFeliz() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio(1L, "ERROR", false)));
        when(envioRepository.save(any(EnvioSII.class))).thenAnswer(inv -> inv.getArgument(0));
        EnvioSII e = service.reintentar(1L);
        assertTrue(e.getReintentado());
        assertEquals("ENVIADO", e.getEstado());
    }
}
