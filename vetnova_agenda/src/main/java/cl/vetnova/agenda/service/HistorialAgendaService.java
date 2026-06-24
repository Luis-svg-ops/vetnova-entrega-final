package cl.vetnova.agenda.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.agenda.model.HistorialAgenda;
import cl.vetnova.agenda.repository.HistorialAgendaRepository;

@Service
public class HistorialAgendaService {
    private static final Logger log = LoggerFactory.getLogger(HistorialAgendaService.class);

    @Autowired
    private HistorialAgendaRepository historialAgendaRepository;

    public HistorialAgenda crear(HistorialAgenda historial){
        log.info("event=registrar_historial citaId={}", historial.getCitaId());
        return historialAgendaRepository.save(historial);
    }

    public List<HistorialAgenda> listar(){
        return historialAgendaRepository.findAll();
    }

    public void eliminar(Long id){
        historialAgendaRepository.deleteById(id);
    }
}