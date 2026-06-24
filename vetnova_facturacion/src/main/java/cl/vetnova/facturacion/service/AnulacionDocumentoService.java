package cl.vetnova.facturacion.service;

import cl.vetnova.facturacion.dto.AnulacionRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.AnulacionDocumento;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.repository.AnulacionDocumentoRepository;
import cl.vetnova.facturacion.repository.DocumentoTributarioRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnulacionDocumentoService {

    private final AnulacionDocumentoRepository anulacionRepository;
    private final DocumentoTributarioRepository documentoRepository;

    public AnulacionDocumentoService(AnulacionDocumentoRepository anulacionRepository,
                                     DocumentoTributarioRepository documentoRepository) {
        this.anulacionRepository = anulacionRepository;
        this.documentoRepository = documentoRepository;
    }

    @Transactional(readOnly = true)
    public AnulacionDocumento buscar(Long id) {
        return anulacionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Anulación no encontrada"));
    }

    @Transactional
    public AnulacionDocumento registrar(AnulacionRequest request) {
        if (request.getDocumentoId() == null) {
            throw new BusinessRuleException("El documentoId es obligatorio");
        }
        DocumentoTributario doc = documentoRepository.findById(request.getDocumentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento tributario no encontrado"));
        if ("ANULADO".equals(doc.getEstadoSII())) {
            throw new ConflictException("El documento ya está anulado");
        }
        if ("RECHAZADO".equals(doc.getEstadoSII())) {
            throw new BusinessRuleException("No se puede anular un documento rechazado por el SII");
        }
        if (request.getAdministradorId() == null) {
            throw new BusinessRuleException("El administradorId es obligatorio");
        }
        // El rol de administrador del solicitante vive en MS Auth → verificación diferida.
        if (request.getMotivo() == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        if (request.getMotivo().trim().isEmpty()) {
            throw new BusinessRuleException("El motivo no puede estar vacío");
        }
        AnulacionDocumento anulacion = new AnulacionDocumento();
        anulacion.setDocumentoId(doc.getId());
        anulacion.setAdministradorId(request.getAdministradorId());
        anulacion.setMotivo(request.getMotivo());
        anulacion.setEstadoSII(doc.getEstadoSII());
        anulacion.setFechaAnulacion(LocalDateTime.now(ZoneOffset.UTC));
        AnulacionDocumento guardada = anulacionRepository.save(anulacion);
        doc.setEstadoSII("ANULADO");
        documentoRepository.save(doc);
        return guardada;
    }

    @Transactional
    public AnulacionDocumento notificarSII(Long id) {
        AnulacionDocumento anulacion = buscar(id);
        DocumentoTributario doc = documentoRepository.findById(anulacion.getDocumentoId()).orElse(null);
        if (doc == null || !"ANULADO".equals(doc.getEstadoSII())) {
            throw new BusinessRuleException("El documento debe estar anulado antes de notificar al SII");
        }
        anulacion.setEstadoSII("NOTIFICADO");
        return anulacionRepository.save(anulacion);
    }
}
