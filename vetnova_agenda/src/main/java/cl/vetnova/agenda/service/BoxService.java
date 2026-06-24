package cl.vetnova.agenda.service;

import java.util.List;

import cl.vetnova.agenda.model.Box;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.agenda.exception.ResourceNotFoundException;

import cl.vetnova.agenda.repository.BoxRepository;

@Service
public class BoxService {
    private static final Logger log = LoggerFactory.getLogger(BoxService.class);

    @Autowired
    private BoxRepository boxRepository;

    public Box crear(Box box){
        log.info("event=crear_box nombre={}", box.getNombre());
        return boxRepository.save(box);
    }

    public List<Box> listar(){
        return boxRepository.findAll();
    }

    public Box reservar(Long id){

        Box box = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box no encontrado con id " + id));

        box.setDisponible(false);
        return boxRepository.save(box);
    }

    public Box liberar(Long id){

        Box box = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box no encontrado con id " + id));

        box.setDisponible(true);
        return boxRepository.save(box);
    }

    public void eliminar(Long id){
        boxRepository.deleteById(id);
    }
}