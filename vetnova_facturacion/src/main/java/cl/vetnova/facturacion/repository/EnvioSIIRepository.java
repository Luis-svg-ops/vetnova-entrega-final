package cl.vetnova.facturacion.repository;

import cl.vetnova.facturacion.model.EnvioSII;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvioSIIRepository extends JpaRepository<EnvioSII, Long> {

    boolean existsByDocumentoIdAndEstado(Long documentoId, String estado);
    List<EnvioSII> findByDocumentoId(Long documentoId);
}
