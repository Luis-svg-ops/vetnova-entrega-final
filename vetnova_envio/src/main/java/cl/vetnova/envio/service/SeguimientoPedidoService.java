package cl.vetnova.envio.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.model.RegistroSeguimiento;
import cl.vetnova.envio.model.SeguimientoPedido;
import cl.vetnova.envio.repository.DespachoRepository;
import cl.vetnova.envio.repository.RegistroSeguimientoRepository;
import cl.vetnova.envio.repository.SeguimientoPedidoRepository;

@Service
public class SeguimientoPedidoService {

    private static final List<String> ESTADOS = List.of("CREADO", "PREPARANDO", "ENVIADO", "ENTREGADO", "CANCELADO");

    @Autowired
    private SeguimientoPedidoRepository seguimientoPedidoRepository;

    @Autowired
    private DespachoRepository despachoRepository;

    @Autowired
    private RegistroSeguimientoRepository registroSeguimientoRepository;

    public List<SeguimientoPedido> listar() {
        return seguimientoPedidoRepository.findAll();
    }

    public SeguimientoPedido obtenerPorId(Long id) {
        return seguimientoPedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SeguimientoPedido no encontrado con id " + id));
    }

    public SeguimientoPedido crear(SeguimientoPedido seguimiento) {
        if (seguimiento.getDespachoId() == null) {
            throw new BusinessRuleException("El despachoId es obligatorio");
        }
        Despacho despacho = despachoRepository.findById(seguimiento.getDespachoId())
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado"));
        if (seguimiento.getOrdenId() == null) {
            throw new BusinessRuleException("El ordenId es obligatorio");
        }
        if (!seguimiento.getOrdenId().equals(despacho.getOrdenId())) {
            throw new BusinessRuleException("La orden no corresponde al despacho indicado");
        }
        if (seguimiento.getEstado() == null) {
            throw new BusinessRuleException("El estado es obligatorio");
        }
        if (!ESTADOS.contains(seguimiento.getEstado())) {
            throw new BusinessRuleException(
                    "Estado no válido. Valores permitidos: CREADO, PREPARANDO, ENVIADO, ENTREGADO, CANCELADO");
        }
        if (ESTADOS.indexOf(seguimiento.getEstado()) > ESTADOS.indexOf(despacho.getEstado())) {
            throw new BusinessRuleException(
                    "El estado del seguimiento no puede adelantarse al estado real del despacho");
        }
        seguimiento.setFechaActualizacion(LocalDateTime.now(ZoneOffset.UTC));
        return seguimientoPedidoRepository.save(seguimiento);
    }

    // CA-SEG-10/11/12: cambia el estado y registra la entrada en el historial.
    public SeguimientoPedido actualizarEstado(Long id, String nuevoEstado, String descripcion) {
        SeguimientoPedido seguimiento = obtenerPorId(id);
        if (nuevoEstado != null && nuevoEstado.equals(seguimiento.getEstado())) {
            throw new BusinessRuleException("El estado nuevo debe ser distinto al estado actual");
        }
        if (nuevoEstado == null || !ESTADOS.contains(nuevoEstado)) {
            throw new BusinessRuleException(
                    "Estado no válido. Valores permitidos: CREADO, PREPARANDO, ENVIADO, ENTREGADO, CANCELADO");
        }
        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);
        seguimiento.setEstado(nuevoEstado);
        seguimiento.setDescripcion(descripcion);
        seguimiento.setFechaActualizacion(ahora);

        RegistroSeguimiento registro = new RegistroSeguimiento();
        registro.setSeguimientoId(id);
        registro.setEstado(nuevoEstado);
        registro.setDescripcion(descripcion);
        registro.setFecha(ahora);
        registroSeguimientoRepository.save(registro);

        return seguimientoPedidoRepository.save(seguimiento);
    }

    public List<RegistroSeguimiento> getHistorial(Long id) {
        obtenerPorId(id);
        return registroSeguimientoRepository.findBySeguimientoIdOrderByFechaAsc(id);
    }

    public SeguimientoPedido actualizar(Long id, SeguimientoPedido datos) {
        SeguimientoPedido existente = obtenerPorId(id);
        existente.setEstado(datos.getEstado());
        existente.setDescripcion(datos.getDescripcion());
        existente.setFechaActualizacion(datos.getFechaActualizacion());
        return seguimientoPedidoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!seguimientoPedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("SeguimientoPedido no encontrado con id " + id);
        }
        seguimientoPedidoRepository.deleteById(id);
    }
}
