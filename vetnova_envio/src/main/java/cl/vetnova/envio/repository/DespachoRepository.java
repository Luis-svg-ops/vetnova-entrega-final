package cl.vetnova.envio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.envio.model.Despacho;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    boolean existsByOrdenId(Long ordenId);
}
