package cl.vetnova.soporte.repository;

import cl.vetnova.soporte.model.CategoriaTicket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaTicketRepository extends JpaRepository<CategoriaTicket, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    List<CategoriaTicket> findAllByOrderByNombreAsc();
}
