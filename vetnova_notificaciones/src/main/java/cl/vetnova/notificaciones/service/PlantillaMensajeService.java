package cl.vetnova.notificaciones.service;

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ConflictException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.PlantillaMensaje;
import cl.vetnova.notificaciones.repository.PlantillaMensajeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlantillaMensajeService {

    private static final Pattern VARIABLE = Pattern.compile("\\{\\{(.*?)\\}\\}");
    private static final String VAR_VALIDA = "[a-zA-Z][a-zA-Z0-9]*";

    private final PlantillaMensajeRepository plantillaRepository;

    public PlantillaMensajeService(PlantillaMensajeRepository plantillaRepository) {
        this.plantillaRepository = plantillaRepository;
    }

    @Transactional(readOnly = true)
    public PlantillaMensaje buscar(Long id) {
        return plantillaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));
    }

    @Transactional
    public PlantillaMensaje crear(PlantillaMensaje request) {
        if (request.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (request.getNombre().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        if (plantillaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ConflictException("Ya existe una plantilla con ese nombre");
        }
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo es obligatorio");
        }
        if (!NotificacionService.TIPOS.contains(request.getTipo())) {
            throw new BusinessRuleException(NotificacionService.TIPO_INVALIDO);
        }
        if (request.getContenido() == null) {
            throw new BusinessRuleException("El contenido es obligatorio");
        }
        if (request.getContenido().trim().isEmpty()) {
            throw new BusinessRuleException("El contenido no puede estar vacío");
        }
        validarVariables(request.getContenido());
        PlantillaMensaje plantilla = new PlantillaMensaje();
        plantilla.setNombre(request.getNombre());
        plantilla.setTipo(request.getTipo());
        plantilla.setContenido(request.getContenido());
        plantilla.setActiva(true);
        return plantillaRepository.save(plantilla);
    }

    @Transactional
    public PlantillaMensaje actualizar(Long id, PlantillaMensaje request) {
        PlantillaMensaje plantilla = buscar(id);
        if (request.getContenido() != null) {
            validarVariables(request.getContenido());
            plantilla.setContenido(request.getContenido());
        }
        return plantillaRepository.save(plantilla);
    }

    @Transactional(readOnly = true)
    public String renderizar(Long id, Map<String, String> valores) {
        PlantillaMensaje plantilla = buscar(id);
        if (!Boolean.TRUE.equals(plantilla.getActiva())) {
            throw new BusinessRuleException("La plantilla está inactiva y no puede usarse");
        }
        String resultado = plantilla.getContenido();
        for (String variable : extraerVariables(plantilla.getContenido())) {
            if (valores == null || !valores.containsKey(variable)) {
                throw new BusinessRuleException("Falta el valor para la variable: " + variable);
            }
            resultado = resultado.replace("{{" + variable + "}}", valores.get(variable));
        }
        return resultado;
    }

    @Transactional
    public void eliminar(Long id) {
        PlantillaMensaje plantilla = buscar(id);
        // La integridad contra HistorialMensaje asociado es cross-flujo → diferida.
        plantillaRepository.delete(plantilla);
    }

    private void validarVariables(String contenido) {
        for (String variable : extraerVariables(contenido)) {
            if (!variable.matches(VAR_VALIDA)) {
                throw new BusinessRuleException("La plantilla contiene variables con formato inválido");
            }
        }
    }

    private List<String> extraerVariables(String contenido) {
        List<String> variables = new ArrayList<>();
        Matcher matcher = VARIABLE.matcher(contenido);
        while (matcher.find()) {
            variables.add(matcher.group(1).trim());
        }
        return variables;
    }
}
