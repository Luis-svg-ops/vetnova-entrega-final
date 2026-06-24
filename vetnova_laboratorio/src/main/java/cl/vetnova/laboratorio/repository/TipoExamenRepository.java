package cl.vetnova.laboratorio.repository;

import cl.vetnova.laboratorio.model.TipoExamen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoExamenRepository extends JpaRepository<TipoExamen, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    List<TipoExamen> findAllByOrderByNombreAsc();
}
