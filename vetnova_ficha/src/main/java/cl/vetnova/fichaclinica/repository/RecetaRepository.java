package cl.vetnova.fichaclinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.fichaclinica.model.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findByFichaIdOrderByFechaEmisionDesc(Long fichaId);
}
