package cl.vetnova.fichaclinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.fichaclinica.model.Procedimiento;

@Repository
public interface ProcedimientoRepository extends JpaRepository<Procedimiento, Long> {

    List<Procedimiento> findByFichaIdOrderByFechaRegistroAsc(Long fichaId);
}
