package cl.vetnova.fichaclinica.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Certificado;
import cl.vetnova.fichaclinica.repository.CertificadoRepository;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.VacunaRepository;

@Service
public class CertificadoService {

    private static final Set<String> TIPOS = Set.of("SALUD", "VACUNACION", "VIAJE", "ADOPCION");

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private VacunaRepository vacunaRepository;

    // CA-CER-01..11: emite un certificado para una ficha existente.
    public Certificado crear(Certificado certificado) {
        if (certificado.getFichaId() == null) {
            throw new BusinessRuleException("El fichaId es obligatorio");
        }
        if (!fichaClinicaRepository.existsById(certificado.getFichaId())) {
            throw new ResourceNotFoundException("Ficha clínica no encontrada");
        }
        if (certificado.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        if (certificado.getTipo() == null) {
            throw new BusinessRuleException("El tipo de certificado es obligatorio");
        }
        if (!TIPOS.contains(certificado.getTipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: SALUD, VACUNACION, VIAJE, ADOPCION");
        }
        if ("VACUNACION".equals(certificado.getTipo())
                && !vacunaRepository.existsByFichaId(certificado.getFichaId())) {
            throw new BusinessRuleException(
                    "No se puede emitir certificado de vacunación sin vacunas registradas en la ficha");
        }
        Date emision = Date.valueOf(LocalDate.now());
        if (certificado.getFechaVencimiento() != null && certificado.getFechaVencimiento().before(emision)) {
            throw new BusinessRuleException("La fecha de vencimiento debe ser posterior a la fecha de emisión");
        }
        certificado.setFechaEmision(emision);
        return certificadoRepository.save(certificado);
    }

    // CA-CER-16: listado de certificados de una ficha.
    public List<Certificado> listarPorFicha(Long fichaId) {
        return certificadoRepository.findByFichaId(fichaId);
    }

    public List<Certificado> listar() {
        return certificadoRepository.findAll();
    }
}
