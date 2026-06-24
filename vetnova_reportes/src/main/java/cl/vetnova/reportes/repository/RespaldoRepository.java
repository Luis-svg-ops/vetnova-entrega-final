package cl.vetnova.reportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.reportes.model.Respaldo;

@Repository
public interface RespaldoRepository extends JpaRepository<Respaldo, Long> {

    boolean existsByEstado(String estado);
}
