package cl.vetnova.envio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.envio.model.GuiaDespacho;

@Repository
public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    boolean existsByDespachoId(Long despachoId);

    boolean existsByFolio(String folio);
}
