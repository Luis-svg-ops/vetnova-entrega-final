package cl.vetnova.facturacion.service;

import cl.vetnova.facturacion.dto.ReporteRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.model.ReporteTributario;
import cl.vetnova.facturacion.repository.DocumentoTributarioRepository;
import cl.vetnova.facturacion.repository.ReporteTributarioRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteTributarioService {

    private static final Set<String> FORMATOS = Set.of("PDF", "EXCEL");
    private static final double IVA_RATE = 0.19;

    private final ReporteTributarioRepository reporteRepository;
    private final DocumentoTributarioRepository documentoRepository;

    public ReporteTributarioService(ReporteTributarioRepository reporteRepository,
                                    DocumentoTributarioRepository documentoRepository) {
        this.reporteRepository = reporteRepository;
        this.documentoRepository = documentoRepository;
    }

    @Transactional(readOnly = true)
    public ReporteTributario buscar(Long id) {
        return reporteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ReporteTributario> listar() {
        return reporteRepository.findAll();
    }

    @Transactional
    public ReporteTributario generar(ReporteRequest request) {
        if (request.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        // La existencia de la sucursal vive en MS Auth/Sucursales → verificación diferida.
        if (request.getPeriodo() == null) {
            throw new BusinessRuleException("El período es obligatorio");
        }
        if (!request.getPeriodo().matches("\\d{4}-\\d{2}")) {
            throw new BusinessRuleException("El período debe tener formato YYYY-MM");
        }
        YearMonth periodo = YearMonth.parse(request.getPeriodo());
        if (periodo.isAfter(YearMonth.now())) {
            throw new BusinessRuleException("No se puede generar reporte para un período futuro");
        }
        if (reporteRepository.existsBySucursalAndPeriodo(request.getSucursal(), request.getPeriodo())) {
            throw new ConflictException("Ya existe un reporte tributario para ese período y sucursal");
        }
        int total = 0;
        double montoNeto = 0;
        for (DocumentoTributario doc : documentoRepository.findBySucursal(request.getSucursal())) {
            if ("ANULADO".equals(doc.getEstadoSII()) || doc.getFechaEmision() == null) {
                continue;
            }
            if (!YearMonth.from(doc.getFechaEmision()).equals(periodo)) {
                continue;
            }
            total++;
            montoNeto += doc.getNeto() != null ? doc.getNeto() : 0.0;
        }
        double montoIva = montoNeto * IVA_RATE;
        ReporteTributario reporte = new ReporteTributario();
        reporte.setSucursal(request.getSucursal());
        reporte.setPeriodo(request.getPeriodo());
        reporte.setTotalDocumentos(total);
        reporte.setMontoNeto(montoNeto);
        reporte.setMontoIva(montoIva);
        reporte.setMontoTotal(montoNeto + montoIva);
        reporte.setGeneradoEn(LocalDateTime.now(ZoneOffset.UTC));
        return reporteRepository.save(reporte);
    }

    @Transactional(readOnly = true)
    public ReporteTributario exportar(Long id, String formato) {
        ReporteTributario reporte = buscar(id);
        if (formato == null || !FORMATOS.contains(formato)) {
            throw new BusinessRuleException("Formato no válido. Valores permitidos: PDF, EXCEL");
        }
        // La generación binaria PDF/EXCEL se delega a un servicio de documentos → diferida.
        return reporte;
    }
}
