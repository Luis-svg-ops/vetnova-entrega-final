package cl.vetnova.soporte.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.vetnova.soporte.dto.PromedioValoracionResponse;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ConflictException;
import cl.vetnova.soporte.exception.ForbiddenException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.model.Valoracion;
import cl.vetnova.soporte.repository.TicketRepository;
import cl.vetnova.soporte.repository.ValoracionRepository;

@Service
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final TicketRepository ticketRepository;

    public ValoracionService(ValoracionRepository valoracionRepository, TicketRepository ticketRepository) {
        this.valoracionRepository = valoracionRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Valoracion crear(Valoracion request) {
        if (request.getTicketId() == null) {
            throw new BusinessRuleException("El ticketId es obligatorio");
        }
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));
        if (!"CERRADO".equals(ticket.getEstado())) {
            throw new BusinessRuleException("Solo se puede valorar un ticket cerrado");
        }
        if (valoracionRepository.existsByTicketId(ticket.getId())) {
            throw new ConflictException("Este ticket ya fue valorado");
        }
        if (request.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        if (!request.getClienteId().equals(ticket.getClienteId())) {
            throw new ForbiddenException("Solo el cliente dueño del ticket puede valorarlo");
        }
        if (request.getPuntuacion() == null) {
            throw new BusinessRuleException("La puntuación es obligatoria");
        }
        if (request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            throw new BusinessRuleException("La puntuación debe estar entre 1 y 5");
        }
        Valoracion valoracion = new Valoracion();
        valoracion.setTicketId(ticket.getId());
        valoracion.setClienteId(request.getClienteId());
        valoracion.setPuntuacion(request.getPuntuacion());
        valoracion.setComentario(request.getComentario());
        valoracion.setSucursalId(ticket.getSucursalId());
        valoracion.setFecha(LocalDateTime.now(ZoneOffset.UTC));
        return valoracionRepository.save(valoracion);
    }

    @Transactional(readOnly = true)
    public Valoracion obtenerPorTicket(Long ticketId) {
        Valoracion valoracion = valoracionRepository.findByTicketId(ticketId);
        if (valoracion == null) {
            throw new ResourceNotFoundException("El ticket no tiene valoración");
        }
        return valoracion;
    }

    @Transactional(readOnly = true)
    public PromedioValoracionResponse promedioPorSucursal(Long sucursalId) {
        List<Valoracion> valoraciones = valoracionRepository.findBySucursalId(sucursalId);
        long total = valoraciones.size();
        double promedio = 0.0;
        if (total > 0) {
            double suma = 0;
            for (Valoracion v : valoraciones) {
                suma += v.getPuntuacion();
            }
            promedio = Math.round((suma / total) * 10.0) / 10.0;
        }
        return new PromedioValoracionResponse(sucursalId, promedio, total);
    }
}
