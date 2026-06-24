package cl.vetnova.facturacion.service;

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
import cl.vetnova.facturacion.util.RutValidator;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentoTributarioService {

    private static final Set<String> TIPOS = Set.of("BOLETA", "FACTURA");
    private static final String TIPO_INVALIDO = "Tipo no válido. Valores permitidos: BOLETA, FACTURA";
    private static final double IVA_RATE = 0.19;

    private final DocumentoTributarioRepository documentoRepository;
    private final FolioRepository folioRepository;
    private final AnulacionDocumentoRepository anulacionRepository;
    private final EnvioSIIRepository envioRepository;
    private final VentasClient ventasClient;

    public DocumentoTributarioService(DocumentoTributarioRepository documentoRepository, FolioRepository folioRepository,
                                      AnulacionDocumentoRepository anulacionRepository, EnvioSIIRepository envioRepository,
                                      VentasClient ventasClient) {
        this.documentoRepository = documentoRepository;
        this.folioRepository = folioRepository;
        this.anulacionRepository = anulacionRepository;
        this.envioRepository = envioRepository;
        this.ventasClient = ventasClient;
    }

    @Transactional(readOnly = true)
    public List<DocumentoTributario> listar() {
        return documentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DocumentoTributario buscarPorId(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento tributario no encontrado"));
    }

    @Transactional
    public DocumentoTributario emitir(DocumentoTributarioRequest request) {
        if (request.getOrdenId() == null) {
            throw new BusinessRuleException("El ordenId es obligatorio");
        }
        if (!ventasClient.ordenExiste(request.getOrdenId())) {
            throw new ResourceNotFoundException("Orden no encontrada");
        }
        if (!ventasClient.ordenConfirmada(request.getOrdenId())) {
            throw new BusinessRuleException("Solo se puede emitir documento para una orden confirmada");
        }
        if (documentoRepository.existsByOrdenId(request.getOrdenId())) {
            throw new ConflictException("La orden ya tiene un documento tributario emitido");
        }
        if (request.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        // La existencia del cliente vive en MS Auth/Clientes → verificación diferida.
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo de documento es obligatorio");
        }
        if (!TIPOS.contains(request.getTipo())) {
            throw new BusinessRuleException(TIPO_INVALIDO);
        }
        if ("FACTURA".equals(request.getTipo()) && (request.getRutReceptor() == null || request.getRutReceptor().trim().isEmpty())) {
            throw new BusinessRuleException("Se requiere RUT del receptor para emitir una factura");
        }
        double neto = request.getNeto() != null ? request.getNeto() : 0.0;
        if (neto < 0) {
            throw new BusinessRuleException("El monto neto no puede ser negativo");
        }
        double iva = neto * IVA_RATE;
        double totalCalculado = neto + iva;
        if (request.getTotal() != null && Math.abs(request.getTotal() - totalCalculado) > 0.01) {
            throw new BusinessRuleException("El total debe ser igual a neto + IVA");
        }
        if (request.getRutEmisor() == null) {
            throw new BusinessRuleException("El RUT del emisor es obligatorio");
        }
        if (!RutValidator.esValido(request.getRutEmisor())) {
            throw new BusinessRuleException("El RUT del emisor no es válido");
        }
        Folio folio = folioRepository
                .findFirstBySucursalAndTipoDocumentoAndActivoTrueAndFoliosRestantesGreaterThan(
                        request.getSucursal(), request.getTipo(), 0)
                .orElseThrow(() -> new BusinessRuleException("No hay folios disponibles para emitir el documento"));
        Integer numeroFolio = folio.getFolioActual();
        folio.setFolioActual(numeroFolio + 1);
        folio.setFoliosRestantes(folio.getFoliosRestantes() - 1);
        folioRepository.save(folio);

        DocumentoTributario doc = new DocumentoTributario();
        doc.setOrdenId(request.getOrdenId());
        doc.setClienteId(request.getClienteId());
        doc.setTipo(request.getTipo());
        doc.setRutEmisor(request.getRutEmisor());
        doc.setRutReceptor(request.getRutReceptor());
        doc.setSucursal(request.getSucursal());
        doc.setFolio(String.valueOf(numeroFolio));
        doc.setNeto(neto);
        doc.setIva(iva);
        doc.setTotal(totalCalculado);
        doc.setEstadoSII("EMITIDO");
        doc.setFechaEmision(LocalDateTime.now(ZoneOffset.UTC));
        return documentoRepository.save(doc);
    }

    @Transactional
    public DocumentoTributario anular(Long id, String motivo) {
        DocumentoTributario doc = buscarPorId(id);
        if ("ANULADO".equals(doc.getEstadoSII())) {
            throw new BusinessRuleException("El documento ya está anulado");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new BusinessRuleException("El motivo de anulación es obligatorio");
        }
        AnulacionDocumento anulacion = new AnulacionDocumento();
        anulacion.setDocumentoId(doc.getId());
        anulacion.setMotivo(motivo);
        anulacion.setEstadoSII(doc.getEstadoSII());
        anulacion.setFechaAnulacion(LocalDateTime.now(ZoneOffset.UTC));
        anulacionRepository.save(anulacion);
        doc.setEstadoSII("ANULADO");
        return documentoRepository.save(doc);
    }

    @Transactional
    public DocumentoTributario enviarAlSII(Long id) {
        DocumentoTributario doc = buscarPorId(id);
        if ("ANULADO".equals(doc.getEstadoSII())) {
            throw new BusinessRuleException("No se puede enviar al SII un documento anulado");
        }
        EnvioSII envio = new EnvioSII();
        envio.setDocumentoId(doc.getId());
        envio.setEstado("ENVIADO");
        envio.setReintentado(false);
        envio.setFechaEnvio(LocalDateTime.now(ZoneOffset.UTC));
        envioRepository.save(envio);
        return doc;
    }
}
