package cl.vetnova.agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.agenda.model.Recordatorio;

@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    boolean existsByCitaIdAndTipo(Long citaId, String tipo);

    List<Recordatorio> findByCitaId(Long citaId);
}
