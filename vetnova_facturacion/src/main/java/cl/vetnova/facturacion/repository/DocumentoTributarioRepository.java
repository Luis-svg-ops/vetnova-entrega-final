package cl.vetnova.facturacion.repository;

import cl.vetnova.facturacion.model.DocumentoTributario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoTributarioRepository extends JpaRepository<DocumentoTributario, Long> {

    boolean existsByOrdenId(Long ordenId);
    List<DocumentoTributario> findByClienteId(Long clienteId);
    List<DocumentoTributario> findByOrdenId(Long ordenId);
    List<DocumentoTributario> findBySucursal(Long sucursal);
    List<DocumentoTributario> findByEstadoSII(String estadoSII);
}
