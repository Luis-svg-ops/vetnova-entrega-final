package cl.vetnova.envio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.envio.model.SeguimientoPedido;

@Repository
public interface SeguimientoPedidoRepository extends JpaRepository<SeguimientoPedido, Long> {
}
