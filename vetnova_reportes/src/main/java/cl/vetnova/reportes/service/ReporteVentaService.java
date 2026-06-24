package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.model.ReporteVenta;
import cl.vetnova.reportes.repository.ReporteRepository;
import cl.vetnova.reportes.repository.ReporteVentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteVentaService {

    private final ReporteVentaRepository ventaRepository;
    private final ReporteRepository reporteRepository;

    public ReporteVentaService(ReporteVentaRepository ventaRepository, ReporteRepository reporteRepository) {
        this.ventaRepository = ventaRepository;
        this.reporteRepository = reporteRepository;
    }

    @Transactional
    public ReporteVenta crear(ReporteVenta request) {
        if (request.getReporteId() == null) {
            throw new BusinessRuleException("El reporteId es obligatorio");
        }
        Reporte reporte = reporteRepository.findById(request.getReporteId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        if (!"VENTA".equals(reporte.getTipo())) {
            throw new BusinessRuleException("El reporte debe ser de tipo VENTA");
        }
        if (ventaRepository.existsByReporteId(reporte.getId())) {
            throw new ConflictException("El reporte ya tiene un detalle de venta asociado");
        }
        double montoTotal = request.getMontoTotal() != null ? request.getMontoTotal() : 0.0;
        if (montoTotal < 0) {
            throw new BusinessRuleException("El monto total no puede ser negativo");
        }
        if (request.getVentaPorProducto() != null && !request.getVentaPorProducto().isEmpty()) {
            double sumaProductos = request.getVentaPorProducto().values().stream().mapToDouble(Double::doubleValue).sum();
            if (Math.abs(montoTotal - sumaProductos) > 0.01) {
                throw new BusinessRuleException("El monto total no coincide con la suma de ventas del período");
            }
        }
        request.setMontoTotal(montoTotal);
        if (request.getTotalOrdenes() == null) {
            request.setTotalOrdenes(0);
        }
        if (request.getProductosVendidos() == null) {
            request.setProductosVendidos(0);
        }
        return ventaRepository.save(request);
    }
}
