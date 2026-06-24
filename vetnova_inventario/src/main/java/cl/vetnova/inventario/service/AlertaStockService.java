package cl.vetnova.inventario.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.dto.AlertaLeidaResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.AlertaStock;
import cl.vetnova.inventario.repository.AlertaStockRepository;
import cl.vetnova.inventario.repository.InventarioRepository;

@Service
public class AlertaStockService {

    private static final Set<String> TIPOS = Set.of("STOCK_MINIMO", "SIN_STOCK");

    @Autowired
    private AlertaStockRepository alertaStockRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<AlertaStock> listar() {
        return alertaStockRepository.findAll();
    }

    public AlertaStock obtenerPorId(Long id) {
        return alertaStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertaStock no encontrado con id " + id));
    }

    public AlertaStock crear(AlertaStock alerta) {
        if (alerta.getInventarioId() == null) {
            throw new BusinessRuleException("El inventarioId es obligatorio");
        }
        if (!inventarioRepository.existsById(alerta.getInventarioId())) {
            throw new ResourceNotFoundException("Inventario no encontrado");
        }
        if (alerta.getTipo() == null) {
            throw new BusinessRuleException("El tipo es obligatorio");
        }
        if (!TIPOS.contains(alerta.getTipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores: STOCK_MINIMO, SIN_STOCK");
        }
        if (alerta.getMensaje() == null) {
            alerta.setMensaje(generarMensaje(alerta));
        }
        alerta.setLeida(false);
        alerta.setFechaGeneracion(LocalDateTime.now(ZoneOffset.UTC));
        return alertaStockRepository.save(alerta);
    }

    public AlertaLeidaResponse marcarLeida(Long id) {
        AlertaStock alerta = obtenerPorId(id);
        if (Boolean.TRUE.equals(alerta.getLeida())) {
            return new AlertaLeidaResponse(alerta, "La alerta ya estaba leída");
        }
        alerta.setLeida(true);
        alertaStockRepository.save(alerta);
        return new AlertaLeidaResponse(alerta, "Alerta marcada como leída");
    }

    public AlertaStock actualizar(Long id, AlertaStock datos) {
        AlertaStock existente = obtenerPorId(id);
        existente.setInventarioId(datos.getInventarioId());
        existente.setSucursal(datos.getSucursal());
        existente.setTipo(datos.getTipo());
        existente.setMensaje(datos.getMensaje());
        existente.setStockActual(datos.getStockActual());
        existente.setStockMinimo(datos.getStockMinimo());
        existente.setLeida(datos.getLeida());
        existente.setFechaGeneracion(datos.getFechaGeneracion());
        return alertaStockRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!alertaStockRepository.existsById(id)) {
            throw new ResourceNotFoundException("AlertaStock no encontrado con id " + id);
        }
        alertaStockRepository.deleteById(id);
    }

    private String generarMensaje(AlertaStock alerta) {
        return "Alerta " + alerta.getTipo() + " para inventario " + alerta.getInventarioId()
                + " en sucursal " + alerta.getSucursal() + ": stock actual " + alerta.getStockActual()
                + ", mínimo " + alerta.getStockMinimo();
    }
}
