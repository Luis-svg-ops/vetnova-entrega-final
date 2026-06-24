package cl.vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.inventario.model.SolicitudReposicion;

@Repository
public interface SolicitudReposicionRepository extends JpaRepository<SolicitudReposicion, Long> {
    boolean existsByInventarioIdAndEstado(Long inventarioId, String estado);
}
