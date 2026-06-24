package cl.vetnova.envio.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.envio.client.VentasClient;
import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ConflictException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.repository.DespachoRepository;

@Service
public class DespachoService {

    private static final Set<String> TIPOS = Set.of("DOMICILIO", "RETIRO", "TRANSFERENCIA");
    private static final String CREADO = "CREADO";
    private static final String PREPARANDO = "PREPARANDO";
    private static final String ENVIADO = "ENVIADO";
    private static final String ENTREGADO = "ENTREGADO";
    private static final String CANCELADO = "CANCELADO";

    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
            CREADO, Set.of(PREPARANDO, CANCELADO),
            PREPARANDO, Set.of(ENVIADO, CANCELADO),
            ENVIADO, Set.of(ENTREGADO, CANCELADO),
            ENTREGADO, Set.of(),
            CANCELADO, Set.of());

    @Autowired
    private DespachoRepository despachoRepository;

    @Autowired
    private VentasClient ventasClient;

    public List<Despacho> listar() {
        return despachoRepository.findAll();
    }

    public Despacho obtenerPorId(Long id) {
        return despachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id " + id));
    }

    public Despacho crear(Despacho despacho) {
        if (despacho.getOrdenId() == null) {
            throw new BusinessRuleException("El ordenId es obligatorio");
        }
        if (!ventasClient.ordenExiste(despacho.getOrdenId())) {
            throw new ResourceNotFoundException("Orden no encontrada");
        }
        if (despachoRepository.existsByOrdenId(despacho.getOrdenId())) {
            throw new ConflictException("La orden ya tiene un despacho asociado");
        }
        if (despacho.getTipo() == null) {
            throw new BusinessRuleException("El tipo de despacho es obligatorio");
        }
        if (!TIPOS.contains(despacho.getTipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: DOMICILIO, RETIRO, TRANSFERENCIA");
        }
        if ("TRANSFERENCIA".equals(despacho.getTipo())) {
            if (despacho.getSucursalOrigen() == null) {
                throw new BusinessRuleException("La sucursal de origen es obligatoria");
            }
            if (despacho.getSucursalOrigen().equals(despacho.getSucursalDestino())) {
                throw new BusinessRuleException("La sucursal de destino debe ser distinta a la de origen");
            }
        }
        if ("DOMICILIO".equals(despacho.getTipo()) && despacho.getResponsable() == null) {
            throw new BusinessRuleException("El responsable es obligatorio para despachos a domicilio");
        }
        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);
        if (despacho.getFechaEstimada() != null && despacho.getFechaEstimada().isBefore(ahora)) {
            throw new BusinessRuleException("La fecha estimada no puede ser anterior a la fecha de creación");
        }
        despacho.setEstado(CREADO);
        despacho.setFechaCreacion(ahora);
        return despachoRepository.save(despacho);
    }

    public Despacho iniciar(Long id) {
        Despacho despacho = obtenerPorId(id);
        if (!CREADO.equals(despacho.getEstado())) {
            throw new BusinessRuleException("No se puede iniciar un despacho que ya fue enviado");
        }
        despacho.setEstado(PREPARANDO);
        return despachoRepository.save(despacho);
    }

    public Despacho enviar(Long id) {
        Despacho despacho = obtenerPorId(id);
        if (!PREPARANDO.equals(despacho.getEstado())) {
            throw new BusinessRuleException("El despacho debe estar en estado PREPARANDO para ser enviado");
        }
        despacho.setEstado(ENVIADO);
        return despachoRepository.save(despacho);
    }

    public Despacho confirmarEntrega(Long id) {
        Despacho despacho = obtenerPorId(id);
        if (!ENVIADO.equals(despacho.getEstado())) {
            throw new BusinessRuleException("El despacho debe estar en estado ENVIADO para confirmar entrega");
        }
        despacho.setEstado(ENTREGADO);
        despacho.setFechaEntrega(LocalDateTime.now(ZoneOffset.UTC));
        return despachoRepository.save(despacho);
    }

    public Despacho cancelar(Long id) {
        Despacho despacho = obtenerPorId(id);
        if (ENTREGADO.equals(despacho.getEstado())) {
            throw new BusinessRuleException("No se puede cancelar un despacho ya entregado");
        }
        despacho.setEstado(CANCELADO);
        return despachoRepository.save(despacho);
    }

    public Despacho actualizarEstado(Long id, String nuevoEstado) {
        Despacho despacho = obtenerPorId(id);
        Set<String> permitidos = TRANSICIONES.getOrDefault(despacho.getEstado(), Set.of());
        if (!permitidos.contains(nuevoEstado)) {
            throw new BusinessRuleException(
                    "Transición de estado no permitida: " + despacho.getEstado() + " → " + nuevoEstado);
        }
        despacho.setEstado(nuevoEstado);
        return despachoRepository.save(despacho);
    }

    public Despacho actualizar(Long id, Despacho datos) {
        Despacho existente = obtenerPorId(id);
        existente.setSucursalOrigen(datos.getSucursalOrigen());
        existente.setSucursalDestino(datos.getSucursalDestino());
        existente.setTipo(datos.getTipo());
        existente.setResponsable(datos.getResponsable());
        existente.setFechaEstimada(datos.getFechaEstimada());
        return despachoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!despachoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Despacho no encontrado con id " + id);
        }
        despachoRepository.deleteById(id);
    }
}
