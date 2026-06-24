package cl.vetnova.envio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.envio.model.ItemDespacho;

@Repository
public interface ItemDespachoRepository extends JpaRepository<ItemDespacho, Long> {

    boolean existsByDespachoIdAndProductoId(Long despachoId, Long productoId);

    long countByDespachoId(Long despachoId);

    List<ItemDespacho> findByDespachoId(Long despachoId);
}
