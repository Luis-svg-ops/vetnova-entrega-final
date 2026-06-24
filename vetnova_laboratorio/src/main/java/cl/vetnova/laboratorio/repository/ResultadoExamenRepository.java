package cl.vetnova.laboratorio.repository;

import cl.vetnova.laboratorio.model.ResultadoExamen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultadoExamenRepository extends JpaRepository<ResultadoExamen, Long> {
    boolean existsByOrdenExamenId(Long ordenExamenId);
}
