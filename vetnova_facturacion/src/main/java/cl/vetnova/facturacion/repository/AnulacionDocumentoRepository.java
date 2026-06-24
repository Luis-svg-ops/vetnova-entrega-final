package cl.vetnova.facturacion.repository;

import cl.vetnova.facturacion.model.AnulacionDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnulacionDocumentoRepository extends JpaRepository<AnulacionDocumento, Long> {

    Optional<AnulacionDocumento> findByDocumentoId(Long documentoId);
    List<AnulacionDocumento> findByAdministradorId(Long administradorId);
}
