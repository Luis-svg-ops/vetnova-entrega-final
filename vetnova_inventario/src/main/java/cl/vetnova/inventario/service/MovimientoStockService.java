package cl.vetnova.inventario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.dto.MovimientoStockRequest;
import cl.vetnova.inventario.dto.MovimientoStockResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.model.TipoMovimiento;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.MovimientoStockRepository;

@Service
public class MovimientoStockService {

    @Autowired
    private MovimientoStockRepository movimientoStockRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private OperacionStock operacionStock;

    public List<MovimientoStock> listar() {
        return movimientoStockRepository.findAll();
    }

    public MovimientoStock obtenerPorId(Long id) {
        return movimientoStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MovimientoStock no encontrado con id " + id));
    }

    public MovimientoStockResponse registrar(MovimientoStockRequest request) {
        if (request.getInventarioId() == null) {
            throw new BusinessRuleException("El inventarioId es obligatorio");
        }
        Inventario inventario = inventarioRepository.findById(request.getInventarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo es obligatorio");
        }
        TipoMovimiento tipo = parseTipo(request.getTipo());
        if (request.getCantidad() == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (request.getCantidad() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        if (request.getMotivo() == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        if (request.getMotivo().isBlank()) {
            throw new BusinessRuleException("El motivo no puede estar vacío");
        }
        if (request.getResponsable() == null) {
            throw new BusinessRuleException("El responsable es obligatorio");
        }
        if (request.getResponsable().isBlank()) {
            throw new BusinessRuleException("El responsable no puede estar vacío");
        }
        if (tipo == TipoMovimiento.SALIDA) {
            int disponible = inventario.getStockDisponible() == null ? 0 : inventario.getStockDisponible();
            if (request.getCantidad() > disponible) {
                throw new BusinessRuleException("Stock insuficiente. Disponible: " + disponible);
            }
        }
        MovimientoStock movimiento = operacionStock.aplicarMovimiento(
                inventario, tipo, request.getCantidad(), request.getMotivo(), request.getResponsable());
        return toResponse(movimiento, inventario.getStockDisponible());
    }

    private TipoMovimiento parseTipo(String tipo) {
        try {
            return TipoMovimiento.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: ENTRADA, SALIDA");
        }
    }

    private MovimientoStockResponse toResponse(MovimientoStock movimiento, Integer stockResultante) {
        MovimientoStockResponse response = new MovimientoStockResponse();
        response.setId(movimiento.getId());
        response.setInventarioId(movimiento.getInventarioId());
        response.setTipo(movimiento.getTipo().name());
        response.setCantidad(movimiento.getCantidad());
        response.setMotivo(movimiento.getMotivo());
        response.setResponsable(movimiento.getResponsable());
        response.setSucursal(movimiento.getSucursal());
        response.setFecha(movimiento.getFecha());
        response.setStockResultante(stockResultante);
        return response;
    }
}
