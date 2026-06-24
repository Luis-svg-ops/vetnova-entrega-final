package cl.vetnova.laboratorio.repository;

import cl.vetnova.laboratorio.model.Procesamiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcesamientoRepository extends JpaRepository<Procesamiento, Long> {
    boolean existsByMuestraId(Long muestraId);
}
