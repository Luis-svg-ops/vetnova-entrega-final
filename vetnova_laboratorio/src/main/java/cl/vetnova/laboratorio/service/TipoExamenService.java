package cl.vetnova.laboratorio.service;

import cl.vetnova.laboratorio.dto.TipoExamenRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.TipoExamen;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;
import cl.vetnova.laboratorio.repository.TipoExamenRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoExamenService {

    private final TipoExamenRepository repository;
    private final OrdenExamenRepository ordenRepository;

    public TipoExamenService(TipoExamenRepository repository, OrdenExamenRepository ordenRepository) {
        this.repository = repository;
        this.ordenRepository = ordenRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoExamen> listar() {
        return repository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public TipoExamen buscarEntidad(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado"));
    }

    @Transactional
    public TipoExamen crear(TipoExamenRequest request) {
        validarNombre(request.getNombre());
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ConflictException("Ya existe un tipo de examen con ese nombre");
        }
        if (request.getTiempoEstimadoHoras() == null) {
            throw new BusinessRuleException("El tiempo estimado es obligatorio");
        }
        if (request.getTiempoEstimadoHoras() <= 0) {
            throw new BusinessRuleException("El tiempo estimado debe ser mayor a 0");
        }
        if (request.getRequiereMuestra() == null) {
            throw new BusinessRuleException("El campo requiereMuestra es obligatorio");
        }
        TipoExamen t = new TipoExamen();
        t.setNombre(request.getNombre());
        t.setDescripcion(request.getDescripcion());
        t.setTiempoEstimadoHoras(request.getTiempoEstimadoHoras());
        t.setRequiereMuestra(request.getRequiereMuestra());
        t.setInstrucciones(request.getInstrucciones());
        return repository.save(t);
    }

    @Transactional
    public TipoExamen actualizar(Long id, TipoExamenRequest request) {
        TipoExamen t = buscarEntidad(id);
        if (request.getNombre() != null) {
            validarNombre(request.getNombre());
            if (repository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
                throw new ConflictException("Ya existe un tipo de examen con ese nombre");
            }
            t.setNombre(request.getNombre());
        }
        if (request.getTiempoEstimadoHoras() != null) {
            if (request.getTiempoEstimadoHoras() <= 0) {
                throw new BusinessRuleException("El tiempo estimado debe ser mayor a 0");
            }
            t.setTiempoEstimadoHoras(request.getTiempoEstimadoHoras());
        }
        if (request.getRequiereMuestra() != null) {
            t.setRequiereMuestra(request.getRequiereMuestra());
        }
        if (request.getDescripcion() != null) {
            t.setDescripcion(request.getDescripcion());
        }
        if (request.getInstrucciones() != null) {
            t.setInstrucciones(request.getInstrucciones());
        }
        return repository.save(t);
    }

    @Transactional
    public void eliminar(Long id) {
        TipoExamen t = buscarEntidad(id);
        if (ordenRepository.existsByTipoExamen_Id(t.getId())) {
            throw new BusinessRuleException("No se puede eliminar un tipo de examen con órdenes asociadas");
        }
        repository.delete(t);
    }

    private void validarNombre(String nombre) {
        if (nombre == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (nombre.trim().isEmpty()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
    }
}
