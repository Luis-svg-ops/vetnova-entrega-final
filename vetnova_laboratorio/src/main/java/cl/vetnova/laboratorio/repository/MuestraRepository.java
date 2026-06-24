package cl.vetnova.laboratorio.repository;

import cl.vetnova.laboratorio.model.Muestra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuestraRepository extends JpaRepository<Muestra, Long> {
    boolean existsByCodigoMuestra(String codigoMuestra);
    boolean existsByOrdenExamenId(Long ordenExamenId);
}
