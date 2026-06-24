package cl.vetnova.envio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.envio.model.RegistroSeguimiento;

@Repository
public interface RegistroSeguimientoRepository extends JpaRepository<RegistroSeguimiento, Long> {

    List<RegistroSeguimiento> findBySeguimientoIdOrderByFechaAsc(Long seguimientoId);
}
