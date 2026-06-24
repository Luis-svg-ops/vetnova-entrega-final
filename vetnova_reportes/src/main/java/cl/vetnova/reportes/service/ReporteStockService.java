package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.model.ReporteStock;
import cl.vetnova.reportes.repository.ReporteRepository;
import cl.vetnova.reportes.repository.ReporteStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteStockService {

    private final ReporteStockRepository stockRepository;
    private final ReporteRepository reporteRepository;

    public ReporteStockService(ReporteStockRepository stockRepository, ReporteRepository reporteRepository) {
        this.stockRepository = stockRepository;
        this.reporteRepository = reporteRepository;
    }

    @Transactional
    public ReporteStock crear(ReporteStock request) {
        if (request.getReporteId() == null) {
            throw new BusinessRuleException("El reporteId es obligatorio");
        }
        Reporte reporte = reporteRepository.findById(request.getReporteId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        if (!"STOCK".equals(reporte.getTipo())) {
            throw new BusinessRuleException("El reporte debe ser de tipo STOCK");
        }
        if (stockRepository.existsByReporteId(reporte.getId())) {
            throw new ConflictException("El reporte ya tiene un detalle de stock asociado");
        }
        int critico = request.getProductosConStockCritico() != null ? request.getProductosConStockCritico() : 0;
        int transito = request.getProductosEnTransito() != null ? request.getProductosEnTransito() : 0;
        if (critico < 0 || transito < 0) {
            throw new BusinessRuleException("Los conteos no pueden ser negativos");
        }
        // La validación de stock contra el inventario real es cross-service (MS Inventario) → diferida.
        request.setProductosConStockCritico(critico);
        request.setProductosEnTransito(transito);
        return stockRepository.save(request);
    }
}
