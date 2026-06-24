package cl.vetnova.auth.repository;

import cl.vetnova.auth.model.AuditoriaAcceso;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaAccesoRepository extends JpaRepository<AuditoriaAcceso, Long> {
    List<AuditoriaAcceso> findByUsuarioIdOrderByTimestampDesc(Long usuarioId);
    List<AuditoriaAcceso> findByAccionOrderByTimestampDesc(String accion);
}
