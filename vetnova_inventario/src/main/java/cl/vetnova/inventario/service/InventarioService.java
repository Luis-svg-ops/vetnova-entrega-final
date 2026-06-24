package cl.vetnova.inventario.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.AlertaStock;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.model.TipoMovimiento;
import cl.vetnova.inventario.repository.AlertaStockRepository;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.MovimientoStockRepository;
import cl.vetnova.inventario.repository.ProductoRepository;

@Service
public class InventarioService implements OperacionStock {

    private static final Set<String> SUCURSALES = Set.of("SANTIAGO", "CHILLAN", "TALCA", "LOS_ANGELES");

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoStockRepository movimientoStockRepository;

    @Autowired
    private AlertaStockRepository alertaStockRepository;

    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    public Inventario obtenerPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con id " + id));
    }

    public Inventario crear(Inventario inventario) {
        if (inventario.getProductoId() == null) {
            throw new BusinessRuleException("El productoId es obligatorio");
        }
        if (!productoRepository.existsById(inventario.getProductoId())) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        if (inventario.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        if (!SUCURSALES.contains(inventario.getSucursal())) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
        }
        if (inventarioRepository.existsByProductoIdAndSucursal(inventario.getProductoId(), inventario.getSucursal())) {
            throw new ConflictException("Ya existe inventario para ese producto en esa sucursal");
        }
        if (inventario.getStockMinimo() != null && inventario.getStockMinimo() < 0) {
            throw new BusinessRuleException("El stock mínimo no puede ser negativo");
        }
        inventario.setStockDisponible(0);
        inventario.setStockTransito(0);
        return inventarioRepository.save(inventario);
    }

    public Inventario actualizar(Long id, Inventario datos) {
        Inventario existente = obtenerPorId(id);
        existente.setStockMinimo(datos.getStockMinimo());
        return inventarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventario no encontrado con id " + id);
        }
        inventarioRepository.deleteById(id);
    }

    public Inventario registrarEntrada(Long id, Integer cantidad, String responsable) {
        Inventario inventario = obtenerPorId(id);
        if (cantidad == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (cantidad <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        if (responsable == null) {
            throw new BusinessRuleException("El responsable es obligatorio");
        }
        aplicarMovimiento(inventario, TipoMovimiento.ENTRADA, cantidad, null, responsable);
        return inventario;
    }

    public Inventario registrarSalida(Long id, Integer cantidad, String motivo) {
        Inventario inventario = obtenerPorId(id);
        if (cantidad == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (cantidad <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        if (motivo == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        int disponible = inventario.getStockDisponible() == null ? 0 : inventario.getStockDisponible();
        if (cantidad > disponible) {
            throw new BusinessRuleException("Stock insuficiente. Disponible: " + disponible);
        }
        aplicarMovimiento(inventario, TipoMovimiento.SALIDA, cantidad, motivo, null);
        return inventario;
    }

    public Inventario ajustarStockMinimo(Long id, Integer minimo) {
        Inventario inventario = obtenerPorId(id);
        if (minimo == null) {
            throw new BusinessRuleException("El stock mínimo es obligatorio");
        }
        if (minimo < 0) {
            throw new BusinessRuleException("El stock mínimo no puede ser negativo");
        }
        inventario.setStockMinimo(minimo);
        return inventarioRepository.save(inventario);
    }

    public int getStockTotal(Long id) {
        Inventario inventario = obtenerPorId(id);
        int disponible = inventario.getStockDisponible() == null ? 0 : inventario.getStockDisponible();
        int transito = inventario.getStockTransito() == null ? 0 : inventario.getStockTransito();
        return disponible + transito;
    }

    @Override
    public MovimientoStock aplicarMovimiento(Inventario inventario, TipoMovimiento tipo, Integer cantidad,
                                             String motivo, String responsable) {
        int disponible = inventario.getStockDisponible() == null ? 0 : inventario.getStockDisponible();
        if (tipo == TipoMovimiento.ENTRADA) {
            inventario.setStockDisponible(disponible + cantidad);
        } else {
            inventario.setStockDisponible(disponible - cantidad);
        }
        inventarioRepository.save(inventario);

        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setInventarioId(inventario.getId());
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setMotivo(motivo);
        movimiento.setResponsable(responsable);
        movimiento.setSucursal(inventario.getSucursal());
        movimiento.setFecha(LocalDateTime.now(ZoneOffset.UTC));
        MovimientoStock guardado = movimientoStockRepository.save(movimiento);

        if (tipo == TipoMovimiento.SALIDA) {
            verificarStockCritico(inventario);
        }
        return guardado;
    }

    public boolean verificarStockCritico(Inventario inventario) {
        int disponible = inventario.getStockDisponible() == null ? 0 : inventario.getStockDisponible();
        int minimo = inventario.getStockMinimo() == null ? 0 : inventario.getStockMinimo();
        if (disponible == 0) {
            generarAlerta(inventario, "SIN_STOCK", disponible, minimo);
            return true;
        }
        if (disponible < minimo) {
            generarAlerta(inventario, "STOCK_MINIMO", disponible, minimo);
            return true;
        }
        return false;
    }

    private void generarAlerta(Inventario inventario, String tipo, int disponible, int minimo) {
        if (alertaStockRepository.existsByInventarioIdAndTipoAndLeidaFalse(inventario.getId(), tipo)) {
            return;
        }
        AlertaStock alerta = new AlertaStock();
        alerta.setInventarioId(inventario.getId());
        alerta.setSucursal(inventario.getSucursal());
        alerta.setTipo(tipo);
        alerta.setStockActual(disponible);
        alerta.setStockMinimo(minimo);
        alerta.setMensaje("Inventario " + inventario.getId() + " en " + inventario.getSucursal()
                + ": stock " + disponible + " (mínimo " + minimo + ")");
        alerta.setLeida(false);
        alerta.setFechaGeneracion(LocalDateTime.now(ZoneOffset.UTC));
        alertaStockRepository.save(alerta);
    }
}
