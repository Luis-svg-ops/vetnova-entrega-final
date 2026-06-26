package cl.vetnova.envio.service;

import cl.vetnova.envio.client.InventarioClient;
import cl.vetnova.envio.dto.CrearTransferenciaRequest;
import cl.vetnova.envio.dto.TransferenciaResponse;
import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.model.TransferenciaSucursal;
import cl.vetnova.envio.repository.RutaDespachoRepository;
import cl.vetnova.envio.repository.TransferenciaSucursalRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferenciaService {
    private static final Logger log = LoggerFactory.getLogger(TransferenciaService.class);

    private final TransferenciaSucursalRepository transferenciaRepository;
    private final InventarioClient inventarioClient;
    private final RutaDespachoRepository rutaDespachoRepository;

    public TransferenciaService(TransferenciaSucursalRepository transferenciaRepository,
                                InventarioClient inventarioClient,
                                RutaDespachoRepository rutaDespachoRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.inventarioClient = inventarioClient;
        this.rutaDespachoRepository = rutaDespachoRepository;
    }

    // Regla de negocio: una transferencia es una SALIDA en la sucursal de origen
    // y una ENTRADA en la sucursal de destino, ambas registradas en Inventario.
    @Transactional
    public TransferenciaResponse crearTransferencia(CrearTransferenciaRequest request) {
        log.info("event=crear_transferencia productoId={} origen={} destino={} cantidad={}",
                request.getIdProducto(), request.getIdSucursalOrigen(),
                request.getIdSucursalDestino(), request.getCantidad());

        if (request.getIdSucursalOrigen().equals(request.getIdSucursalDestino())) {
            throw new BusinessRuleException("La sucursal de origen y destino no pueden ser la misma");
        }
        if (!rutaDespachoRepository.existsBySucursalOrigenAndSucursalDestino(
                request.getIdSucursalOrigen(), request.getIdSucursalDestino())) {
            throw new BusinessRuleException("No existe una ruta de despacho entre "
                    + request.getIdSucursalOrigen() + " y " + request.getIdSucursalDestino());
        }

        String motivo = "Transferencia entre sucursales " + request.getIdSucursalOrigen()
                + " -> " + request.getIdSucursalDestino();
        inventarioClient.registrarMovimiento(request.getIdProducto(), request.getIdSucursalOrigen(),
                "SALIDA", request.getCantidad(), motivo);
        inventarioClient.registrarMovimiento(request.getIdProducto(), request.getIdSucursalDestino(),
                "ENTRADA", request.getCantidad(), motivo);

        TransferenciaSucursal transferencia = new TransferenciaSucursal();
        transferencia.setIdProducto(request.getIdProducto());
        transferencia.setIdSucursalOrigen(request.getIdSucursalOrigen());
        transferencia.setIdSucursalDestino(request.getIdSucursalDestino());
        transferencia.setCantidad(request.getCantidad());
        transferencia.setObservacion(request.getObservacion());

        TransferenciaSucursal guardada = transferenciaRepository.save(transferencia);
        log.info("event=transferencia_creada transferenciaId={}", guardada.getId());
        return toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<TransferenciaResponse> listar() {
        return transferenciaRepository.findAll().stream().map(this::toResponse).toList();
    }

    private TransferenciaResponse toResponse(TransferenciaSucursal t) {
        TransferenciaResponse response = new TransferenciaResponse();
        response.setId(t.getId());
        response.setIdProducto(t.getIdProducto());
        response.setIdSucursalOrigen(t.getIdSucursalOrigen());
        response.setIdSucursalDestino(t.getIdSucursalDestino());
        response.setCantidad(t.getCantidad());
        response.setEstado(t.getEstado());
        response.setObservacion(t.getObservacion());
        response.setFecha(t.getFecha());
        return response;
    }
}
