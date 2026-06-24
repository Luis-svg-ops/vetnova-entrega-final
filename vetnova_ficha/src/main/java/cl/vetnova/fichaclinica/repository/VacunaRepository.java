package cl.vetnova.fichaclinica.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.fichaclinica.model.Vacuna;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Long> {

    boolean existsByFichaIdAndNombreAndFechaAplicacion(Long fichaId, String nombre, Date fechaAplicacion);

    boolean existsByFichaId(Long fichaId);

    List<Vacuna> findByFichaIdOrderByFechaAplicacionAsc(Long fichaId);
}
