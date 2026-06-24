package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Respaldo;
import cl.vetnova.reportes.repository.RespaldoRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RespaldoService {

    private static final Set<String> TIPOS = Set.of("COMPLETO", "INCREMENTAL");

    private final RespaldoRepository respaldoRepository;

    public RespaldoService(RespaldoRepository respaldoRepository) {
        this.respaldoRepository = respaldoRepository;
    }

    @Transactional(readOnly = true)
    public Respaldo buscar(Long id) {
        return respaldoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Respaldo no encontrado"));
    }

    @Transactional
    public Respaldo ejecutar(Respaldo request) {
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo es obligatorio");
        }
        if (!TIPOS.contains(request.getTipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: COMPLETO, INCREMENTAL");
        }
        if (request.getAlcance() == null) {
            throw new BusinessRuleException("El alcance es obligatorio");
        }
        if (request.getAlcance().trim().isEmpty()) {
            throw new BusinessRuleException("El alcance no puede estar vacío");
        }
        if (request.getEjecutadoPor() == null) {
            throw new BusinessRuleException("El ejecutadoPor es obligatorio");
        }
        // El rol ADMIN_SISTEMA del ejecutor vive en MS Auth → verificación diferida.
        if (request.getUbicacion() == null) {
            throw new BusinessRuleException("La ubicación del respaldo es obligatoria");
        }
        if (request.getTamanoBytes() != null && request.getTamanoBytes() < 0) {
            throw new BusinessRuleException("El tamaño no puede ser negativo");
        }
        if (respaldoRepository.existsByEstado("EN_CURSO")) {
            throw new ConflictException("Ya hay un respaldo en curso");
        }
        request.setId(null);
        request.setEstado("EN_CURSO");
        request.setFechaFin(null);
        request.setFechaInicio(LocalDateTime.now(ZoneOffset.UTC));
        return respaldoRepository.save(request);
    }

    @Transactional(readOnly = true)
    public boolean verificarIntegridad(Long id) {
        Respaldo respaldo = buscar(id);
        return "COMPLETADO".equals(respaldo.getEstado());
    }

    @Transactional(readOnly = true)
    public Respaldo restaurar(Long id) {
        Respaldo respaldo = buscar(id);
        if (!"COMPLETADO".equals(respaldo.getEstado())) {
            throw new BusinessRuleException("No se puede restaurar desde un respaldo no íntegro o fallido");
        }
        // Restauración efectiva y notificación a ADMIN_SISTEMA → diferidas.
        return respaldo;
    }
}
