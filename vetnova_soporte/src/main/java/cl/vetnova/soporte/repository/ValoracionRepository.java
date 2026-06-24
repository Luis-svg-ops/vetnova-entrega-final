package cl.vetnova.soporte.repository;

import cl.vetnova.soporte.model.Valoracion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    boolean existsByTicketId(Long ticketId);
    Valoracion findByTicketId(Long ticketId);
    List<Valoracion> findBySucursalId(Long sucursalId);
}
