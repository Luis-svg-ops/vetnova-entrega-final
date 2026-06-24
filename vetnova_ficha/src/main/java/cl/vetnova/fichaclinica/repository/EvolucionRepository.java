package cl.vetnova.fichaclinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.fichaclinica.model.Evolucion;

@Repository
public interface EvolucionRepository extends JpaRepository<Evolucion, Long> {

    List<Evolucion> findByFichaIdOrderByFechaRegistroAsc(Long fichaId);
}
