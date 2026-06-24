package cl.vetnova.inventario.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.dto.CancelacionResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.TransferenciaStock;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.ProductoRepository;
import cl.vetnova.inventario.repository.TransferenciaStockRepository;

@Service
public class TransferenciaStockService {

    private static final String CREADA = "creada";
    private static final String EN_TRANSITO = "en tránsito";
    private static final String RECIBIDA = "recibida";
    private static final String CANCELADA = "cancelada";

    @Autowired
    private TransferenciaStockRepository transferenciaStockRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<TransferenciaStock> listar() {
        return transferenciaStockRepository.findAll();
    }

    public TransferenciaStock obtenerPorId(Long id) {
        return transferenciaStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransferenciaStock no encontrado con id " + id));
    }

    public TransferenciaStock crear(TransferenciaStock transferencia) {
        if (transferencia.getProductoId() == null) {
            throw new BusinessRuleException("El productoId es obligatorio");
        }
        if (!productoRepository.existsById(transferencia.getProductoId())) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        if (transferencia.getSucursalOrigen() == null) {
            throw new BusinessRuleException("La sucursal de origen es obligatoria");
        }
        if (transferencia.getSucursalDestino() == null) {
            throw new BusinessRuleException("La sucursal de destino es obligatoria");
        }
        if (transferencia.getSucursalOrigen().equals(transferencia.getSucursalDestino())) {
            throw new BusinessRuleException("La sucursal de origen y destino no pueden ser la misma");
        }
        if (transferencia.getCantidad() == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (transferencia.getCantidad() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }

        Inventario origen = buscarInventario(transferencia.getProductoId(), transferencia.getSucursalOrigen());
        int disponibleOrigen = valor(origen.getStockDisponible());
        if (transferencia.getCantidad() > disponibleOrigen) {
            throw new BusinessRuleException("Stock insuficiente en origen. Disponible: " + disponibleOrigen);
        }
        origen.setStockDisponible(disponibleOrigen - transferencia.getCantidad());
        inventarioRepository.save(origen);

        Inventario destino = inventarioRepository
                .findByProductoIdAndSucursal(transferencia.getProductoId(), transferencia.getSucursalDestino())
                .orElseGet(() -> nuevoInventario(transferencia.getProductoId(), transferencia.getSucursalDestino()));
        destino.setStockTransito(valor(destino.getStockTransito()) + transferencia.getCantidad());
        inventarioRepository.save(destino);

        transferencia.setEstado(CREADA);
        transferencia.setFechaSolicitud(LocalDateTime.now(ZoneOffset.UTC));
        return transferenciaStockRepository.save(transferencia);
    }

    public TransferenciaStock iniciar(Long id) {
        TransferenciaStock transferencia = obtenerPorId(id);
        if (EN_TRANSITO.equals(transferencia.getEstado())) {
            throw new BusinessRuleException("La transferencia ya está en tránsito");
        }
        transferencia.setEstado(EN_TRANSITO);
        return transferenciaStockRepository.save(transferencia);
    }

    public TransferenciaStock confirmarRecepcion(Long id) {
        TransferenciaStock transferencia = obtenerPorId(id);
        if (RECIBIDA.equals(transferencia.getEstado())) {
            throw new BusinessRuleException("La transferencia ya fue recibida");
        }
        Inventario destino = buscarInventario(transferencia.getProductoId(), transferencia.getSucursalDestino());
        destino.setStockTransito(valor(destino.getStockTransito()) - transferencia.getCantidad());
        destino.setStockDisponible(valor(destino.getStockDisponible()) + transferencia.getCantidad());
        inventarioRepository.save(destino);

        transferencia.setEstado(RECIBIDA);
        transferencia.setFechaConfirmacion(LocalDateTime.now(ZoneOffset.UTC));
        return transferenciaStockRepository.save(transferencia);
    }

    public CancelacionResponse cancelar(Long id) {
        TransferenciaStock transferencia = obtenerPorId(id);
        if (RECIBIDA.equals(transferencia.getEstado())) {
            throw new BusinessRuleException("No se puede cancelar una transferencia ya recibida");
        }
        if (CANCELADA.equals(transferencia.getEstado())) {
            return new CancelacionResponse(transferencia, "La transferencia ya estaba cancelada");
        }

        Inventario origen = buscarInventario(transferencia.getProductoId(), transferencia.getSucursalOrigen());
        origen.setStockDisponible(valor(origen.getStockDisponible()) + transferencia.getCantidad());
        inventarioRepository.save(origen);

        Inventario destino = buscarInventario(transferencia.getProductoId(), transferencia.getSucursalDestino());
        destino.setStockTransito(valor(destino.getStockTransito()) - transferencia.getCantidad());
        inventarioRepository.save(destino);

        transferencia.setEstado(CANCELADA);
        transferenciaStockRepository.save(transferencia);
        return new CancelacionResponse(transferencia, "Transferencia cancelada");
    }

    public TransferenciaStock actualizar(Long id, TransferenciaStock datos) {
        TransferenciaStock existente = obtenerPorId(id);
        existente.setProductoId(datos.getProductoId());
        existente.setSucursalOrigen(datos.getSucursalOrigen());
        existente.setSucursalDestino(datos.getSucursalDestino());
        existente.setCantidad(datos.getCantidad());
        existente.setEstado(datos.getEstado());
        existente.setFechaSolicitud(datos.getFechaSolicitud());
        existente.setFechaConfirmacion(datos.getFechaConfirmacion());
        return transferenciaStockRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!transferenciaStockRepository.existsById(id)) {
            throw new ResourceNotFoundException("TransferenciaStock no encontrado con id " + id);
        }
        transferenciaStockRepository.deleteById(id);
    }

    private Inventario buscarInventario(Long productoId, String sucursal) {
        return inventarioRepository.findByProductoIdAndSucursal(productoId, sucursal)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado en sucursal " + sucursal));
    }

    private Inventario nuevoInventario(Long productoId, String sucursal) {
        Inventario inventario = new Inventario();
        inventario.setProductoId(productoId);
        inventario.setSucursal(sucursal);
        inventario.setStockDisponible(0);
        inventario.setStockMinimo(0);
        inventario.setStockTransito(0);
        return inventario;
    }

    private int valor(Integer numero) {
        return numero == null ? 0 : numero;
    }
}
