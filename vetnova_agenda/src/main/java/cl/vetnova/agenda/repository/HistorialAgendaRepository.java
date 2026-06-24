package cl.vetnova.agenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.agenda.model.HistorialAgenda;

@Repository
public interface HistorialAgendaRepository extends JpaRepository<HistorialAgenda, Long> {

}