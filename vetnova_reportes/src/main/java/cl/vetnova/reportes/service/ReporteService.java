package cl.vetnova.reportes.service;

import cl.vetnova.reportes.client.AuthClient;
import cl.vetnova.reportes.dto.ReporteRequest;
import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.repository.ReporteRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    private static final Set<String> TIPOS = Set.of("ATENCION", "VENTA", "STOCK");
    private static final Set<String> FORMATOS = Set.of("PDF", "EXCEL");

    private final AuthClient authClient;

    public ReporteService(ReporteRepository reporteRepository, AuthClient authClient) {
    this.reporteRepository = reporteRepository;
    this.authClient = authClient;
    }

    private final ReporteRepository reporteRepository;

    

    @Transactional(readOnly = true)
    public List<Reporte> listar() {
        return reporteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reporte buscar(Long id) {
        return reporteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Reporte> filtrarPorSucursal(String sucursal) {
        return reporteRepository.findBySucursal(sucursal);
    }

    @Transactional
    public Reporte generar(ReporteRequest request) {
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo de reporte es obligatorio");
        }
        if (!TIPOS.contains(request.getTipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: ATENCION, VENTA, STOCK");
        }
        if (request.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        // La existencia de la sucursal vive en MS Auth/Sucursales → verificación diferida.
        if (request.getDesde() == null) {
            throw new BusinessRuleException("La fecha de inicio es obligatoria");
        }
        if (request.getHasta() == null) {
            throw new BusinessRuleException("La fecha de fin es obligatoria");
        }
        if (request.getDesde().isAfter(request.getHasta())) {
            throw new BusinessRuleException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (request.getGeneradoPor() == null) {
            throw new BusinessRuleException("El generadoPor es obligatorio");
        }
        if (!authClient.usuarioExiste(request.getGeneradoPor())) {
            throw new ResourceNotFoundException("Usuario generador no encontrado");
        }
        Reporte reporte = new Reporte();
        reporte.setTipo(request.getTipo());
        reporte.setSucursal(request.getSucursal());
        reporte.setDesde(request.getDesde());
        reporte.setHasta(request.getHasta());
        reporte.setGeneradoPor(request.getGeneradoPor());
        reporte.setGeneradoEn(LocalDateTime.now(ZoneOffset.UTC));
        // El estado VACIO depende de la consolidación cross-service (Citas/Ordenes/Inventario) → diferido.
        reporte.setEstado("GENERADO");
        return reporteRepository.save(reporte);
    }

    @Transactional(readOnly = true)
    public Reporte exportar(Long id, String formato) {
        Reporte reporte = buscar(id);
        if (formato == null || !FORMATOS.contains(formato)) {
            throw new BusinessRuleException("Formato no válido. Valores permitidos: PDF, EXCEL");
        }
        // La generación binaria PDF/EXCEL se delega a un servicio de documentos → diferida.
        return reporte;
    }
}
