package cl.vetnova.facturacion.service;

import cl.vetnova.facturacion.dto.EnvioSIIRequest;
import cl.vetnova.facturacion.dto.ProcesarRespuestaSiiRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.model.EnvioSII;
import cl.vetnova.facturacion.repository.DocumentoTributarioRepository;
import cl.vetnova.facturacion.repository.EnvioSIIRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvioSIIService {

    private final EnvioSIIRepository envioRepository;
    private final DocumentoTributarioRepository documentoRepository;

    public EnvioSIIService(EnvioSIIRepository envioRepository, DocumentoTributarioRepository documentoRepository) {
        this.envioRepository = envioRepository;
        this.documentoRepository = documentoRepository;
    }

    @Transactional(readOnly = true)
    public EnvioSII buscar(Long id) {
        return envioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Envío SII no encontrado"));
    }

    @Transactional
    public EnvioSII enviar(EnvioSIIRequest request) {
        if (request.getDocumentoId() == null) {
            throw new BusinessRuleException("El documentoId es obligatorio");
        }
        DocumentoTributario doc = documentoRepository.findById(request.getDocumentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento tributario no encontrado"));
        if ("ANULADO".equals(doc.getEstadoSII())) {
            throw new BusinessRuleException("No se puede enviar al SII un documento anulado");
        }
        if (envioRepository.existsByDocumentoIdAndEstado(doc.getId(), "ACEPTADO")) {
            throw new ConflictException("El documento ya fue enviado y aceptado por el SII");
        }
        EnvioSII envio = new EnvioSII();
        envio.setDocumentoId(doc.getId());
        envio.setEstado("ENVIADO");
        envio.setReintentado(false);
        envio.setFechaEnvio(LocalDateTime.now(ZoneOffset.UTC));
        return envioRepository.save(envio);
    }

    @Transactional
    public EnvioSII procesarRespuesta(Long id, ProcesarRespuestaSiiRequest request) {
        EnvioSII envio = buscar(id);
        if (request == null || request.getCodigo() == null) {
            throw new BusinessRuleException("La respuesta del SII no puede ser nula");
        }
        boolean aceptado = "ACEPTADO".equals(request.getCodigo());
        envio.setEstado(aceptado ? "ACEPTADO" : "RECHAZADO");
        envio.setRespuestaCodigo(request.getCodigo());
        envio.setRespuestaDescripcion(request.getDescripcion());
        documentoRepository.findById(envio.getDocumentoId()).ifPresent(doc -> {
            doc.setEstadoSII(aceptado ? "ACEPTADO" : "RECHAZADO");
            documentoRepository.save(doc);
        });
        return envioRepository.save(envio);
    }

    @Transactional
    public EnvioSII reintentar(Long id) {
        EnvioSII envio = buscar(id);
        if ("ACEPTADO".equals(envio.getEstado())) {
            throw new BusinessRuleException("No se puede reintentar un envío ya aceptado");
        }
        if (Boolean.TRUE.equals(envio.getReintentado())) {
            throw new BusinessRuleException("Se alcanzó el límite de reintentos para este envío");
        }
        envio.setReintentado(true);
        envio.setEstado("ENVIADO");
        envio.setFechaEnvio(LocalDateTime.now(ZoneOffset.UTC));
        return envioRepository.save(envio);
    }
}
