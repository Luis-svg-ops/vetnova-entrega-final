package cl.vetnova.reportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.reportes.model.IncidenteSistema;

@Repository
public interface IncidenteSistemaRepository extends JpaRepository<IncidenteSistema, Long> {

    boolean existsByMicroservicioAndTipoAndEstado(String microservicio, String tipo, String estado);
}
