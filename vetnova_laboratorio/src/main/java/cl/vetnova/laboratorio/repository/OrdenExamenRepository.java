package cl.vetnova.laboratorio.repository;

import cl.vetnova.laboratorio.model.OrdenExamen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenExamenRepository extends JpaRepository<OrdenExamen, Long> {
    List<OrdenExamen> findByMascotaIdOrderByFechaSolicitudDesc(Long mascotaId);
    boolean existsByTipoExamen_Id(Long tipoExamenId);
}
