package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.model.ReporteAtencion;
import cl.vetnova.reportes.repository.ReporteAtencionRepository;
import cl.vetnova.reportes.repository.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteAtencionService {

    private final ReporteAtencionRepository atencionRepository;
    private final ReporteRepository reporteRepository;

    public ReporteAtencionService(ReporteAtencionRepository atencionRepository, ReporteRepository reporteRepository) {
        this.atencionRepository = atencionRepository;
        this.reporteRepository = reporteRepository;
    }

    @Transactional
    public ReporteAtencion crear(ReporteAtencion request) {
        if (request.getReporteId() == null) {
            throw new BusinessRuleException("El reporteId es obligatorio");
        }
        Reporte reporte = reporteRepository.findById(request.getReporteId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        if (!"ATENCION".equals(reporte.getTipo())) {
            throw new BusinessRuleException("El reporte debe ser de tipo ATENCION");
        }
        if (atencionRepository.existsByReporteId(reporte.getId())) {
            throw new ConflictException("El reporte ya tiene un detalle de atención asociado");
        }
        int realizadas = request.getCitasRealizadas() != null ? request.getCitasRealizadas() : 0;
        int canceladas = request.getCitasCanceladas() != null ? request.getCitasCanceladas() : 0;
        int ausentes = request.getCitasAusentes() != null ? request.getCitasAusentes() : 0;
        if (realizadas < 0 || canceladas < 0 || ausentes < 0) {
            throw new BusinessRuleException("Los conteos no pueden ser negativos");
        }
        int suma = realizadas + canceladas + ausentes;
        if (request.getTotalCitas() != null && request.getTotalCitas() != suma) {
            throw new BusinessRuleException("La suma de subtotales no coincide con el total de citas");
        }
        request.setCitasRealizadas(realizadas);
        request.setCitasCanceladas(canceladas);
        request.setCitasAusentes(ausentes);
        request.setTotalCitas(suma);
        return atencionRepository.save(request);
    }
}
