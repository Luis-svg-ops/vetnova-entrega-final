package cl.vetnova.agenda.service;

import java.util.List;

import cl.vetnova.agenda.model.Box;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;

import cl.vetnova.agenda.repository.BoxRepository;

// Gestiona los boxes (consultorios físicos) de la clínica; evita que dos citas usen el mismo espacio simultáneamente
@Service
public class BoxService {
    private static final Logger log = LoggerFactory.getLogger(BoxService.class);

    @Autowired
    private BoxRepository boxRepository;

    // Todo box nuevo comienza disponible
    public Box crear(Box box) {
        if (box.getNombre() == null || box.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre del box es obligatorio");
        }
        if (box.getSucursal() == null || box.getSucursal().isBlank()) {
            throw new BusinessRuleException("La sucursal del box es obligatoria");
        }
        box.setDisponible(true);
        log.info("event=crear_box nombre={}", box.getNombre());
        return boxRepository.save(box);
    }

    public List<Box> listar() {
        return boxRepository.findAll();
    }

    // Marca el box como ocupado; falla si ya estaba reservado (no se puede reservar dos veces)
    public Box reservar(Long id) {
        Box box = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box no encontrado con id " + id));
        if (!Boolean.TRUE.equals(box.getDisponible())) {
            throw new BusinessRuleException("El box ya está reservado");
        }
        box.setDisponible(false);
        return boxRepository.save(box);
    }

    // Devuelve el box al pool de disponibles; falla si ya estaba libre
    public Box liberar(Long id) {
        Box box = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box no encontrado con id " + id));
        if (Boolean.TRUE.equals(box.getDisponible())) {
            throw new BusinessRuleException("El box ya está disponible");
        }
        box.setDisponible(true);
        return boxRepository.save(box);
    }

    // Hard delete: los boxes no tienen historial clínico, se pueden eliminar físicamente
    public void eliminar(Long id) {
        boxRepository.deleteById(id);
    }
}
