package cl.vetnova.envio.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ConflictException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.RutaDespacho;
import cl.vetnova.envio.repository.RutaDespachoRepository;

@Service
public class RutaDespachoService {

    private static final Set<String> SUCURSALES = Set.of("1", "2", "3", "4");

    @Autowired
    private RutaDespachoRepository rutaDespachoRepository;

    public List<RutaDespacho> listar() {
        return rutaDespachoRepository.findAll();
    }

    public RutaDespacho obtenerPorId(Long id) {
        return rutaDespachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RutaDespacho no encontrado con id " + id));
    }

    public RutaDespacho crear(RutaDespacho ruta) {
        if (ruta.getSucursalOrigen() == null) {
            throw new BusinessRuleException("La sucursal de origen es obligatoria");
        }
        if (!SUCURSALES.contains(ruta.getSucursalOrigen())) {
            throw new ResourceNotFoundException("Sucursal de origen no encontrada");
        }
        if (ruta.getSucursalDestino() == null) {
            throw new BusinessRuleException("La sucursal de destino es obligatoria");
        }
        if (!SUCURSALES.contains(ruta.getSucursalDestino())) {
            throw new ResourceNotFoundException("Sucursal de destino no encontrada");
        }
        if (ruta.getSucursalOrigen().equals(ruta.getSucursalDestino())) {
            throw new BusinessRuleException("La sucursal de destino debe ser distinta a la de origen");
        }
        if (rutaDespachoRepository.existsBySucursalOrigenAndSucursalDestino(
                ruta.getSucursalOrigen(), ruta.getSucursalDestino())) {
            throw new ConflictException("Ya existe una ruta entre estas sucursales");
        }
        if (ruta.getDistanciaKm() < 0) {
            throw new BusinessRuleException("La distancia no puede ser negativa");
        }
        if (ruta.getTiempoEstimadoMin() < 0) {
            throw new BusinessRuleException("El tiempo estimado no puede ser negativo");
        }
        ruta.setActiva(true);
        return rutaDespachoRepository.save(ruta);
    }

    // CA-RUT-13/14: retorna la ruta activa con menor tiempo estimado entre dos sucursales.
    public RutaDespacho optimizar(String sucursalOrigen, String sucursalDestino) {
        List<RutaDespacho> rutas = rutaDespachoRepository
                .findBySucursalOrigenAndSucursalDestinoAndActivaTrue(sucursalOrigen, sucursalDestino);
        if (rutas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró ruta activa entre las sucursales indicadas");
        }
        return rutas.stream().min(Comparator.comparingInt(RutaDespacho::getTiempoEstimadoMin)).get();
    }

    public int calcularTiempoEstimado(Long id) {
        return obtenerPorId(id).getTiempoEstimadoMin();
    }

    public RutaDespacho actualizar(Long id, RutaDespacho datos) {
        RutaDespacho existente = obtenerPorId(id);
        existente.setDistanciaKm(datos.getDistanciaKm());
        existente.setTiempoEstimadoMin(datos.getTiempoEstimadoMin());
        existente.setActiva(datos.getActiva());
        return rutaDespachoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!rutaDespachoRepository.existsById(id)) {
            throw new ResourceNotFoundException("RutaDespacho no encontrado con id " + id);
        }
        rutaDespachoRepository.deleteById(id);
    }
}
