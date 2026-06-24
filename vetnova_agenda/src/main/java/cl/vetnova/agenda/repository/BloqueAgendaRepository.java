package cl.vetnova.agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.agenda.model.BloqueAgenda;

@Repository
public interface BloqueAgendaRepository extends JpaRepository<BloqueAgenda, Long> {

    List<BloqueAgenda> findByVeterinarioId(Long veterinarioId);
}
