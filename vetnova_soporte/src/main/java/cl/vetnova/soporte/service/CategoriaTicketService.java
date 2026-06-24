package cl.vetnova.soporte.service;

import cl.vetnova.soporte.dto.CategoriaTicketRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ConflictException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.CategoriaTicket;
import cl.vetnova.soporte.repository.CategoriaTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaTicketService {

    private static final Set<String> PRIORIDADES = Set.of("BAJA", "MEDIA", "ALTA", "CRITICA");
    private static final String PRIORIDAD_INVALIDA = "Prioridad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA";

    private final CategoriaTicketRepository repository;
    private final TicketRepository ticketRepository;

    public CategoriaTicketService(CategoriaTicketRepository repository, TicketRepository ticketRepository) {
        this.repository = repository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaTicket> listar() {
        return repository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public CategoriaTicket buscarEntidad(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    @Transactional
    public CategoriaTicket crear(CategoriaTicketRequest request) {
        validarNombre(request.getNombre());
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }
        validarPrioridad(request.getPrioridadDefault());
        CategoriaTicket c = new CategoriaTicket();
        c.setNombre(request.getNombre());
        c.setDescripcion(request.getDescripcion());
        c.setAreaPorDefecto(request.getAreaPorDefecto());
        c.setPrioridadDefault(request.getPrioridadDefault());
        return repository.save(c);
    }

    @Transactional
    public CategoriaTicket actualizar(Long id, CategoriaTicketRequest request) {
        CategoriaTicket c = buscarEntidad(id);
        if (request.getNombre() != null) {
            validarNombre(request.getNombre());
            if (repository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
                throw new ConflictException("Ya existe una categoría con ese nombre");
            }
            c.setNombre(request.getNombre());
        }
        if (request.getPrioridadDefault() != null) {
            validarPrioridad(request.getPrioridadDefault());
            c.setPrioridadDefault(request.getPrioridadDefault());
        }
        if (request.getDescripcion() != null) {
            c.setDescripcion(request.getDescripcion());
        }
        if (request.getAreaPorDefecto() != null) {
            c.setAreaPorDefecto(request.getAreaPorDefecto());
        }
        return repository.save(c);
    }

    @Transactional
    public void eliminar(Long id) {
        CategoriaTicket c = buscarEntidad(id);
        if (ticketRepository.existsByCategoria_Id(c.getId())) {
            throw new BusinessRuleException("No se puede eliminar una categoría con tickets asociados");
        }
        repository.delete(c);
    }

    private void validarNombre(String nombre) {
        if (nombre == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (nombre.trim().isEmpty()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
    }

    private void validarPrioridad(String prioridad) {
        if (prioridad != null && !PRIORIDADES.contains(prioridad)) {
            throw new BusinessRuleException(PRIORIDAD_INVALIDA);
        }
    }
}
