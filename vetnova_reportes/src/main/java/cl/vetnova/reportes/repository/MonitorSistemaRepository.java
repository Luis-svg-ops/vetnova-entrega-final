package cl.vetnova.reportes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.reportes.model.MonitorSistema;

@Repository
public interface MonitorSistemaRepository extends JpaRepository<MonitorSistema, Long> {

    List<MonitorSistema> findByMicroservicioOrderByUltimoChequeoDesc(String microservicio);
}
