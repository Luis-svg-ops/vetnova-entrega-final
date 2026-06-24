package cl.vetnova.facturacion.repository;

import cl.vetnova.facturacion.model.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<Folio, Long> {

    List<Folio> findBySucursalAndTipoDocumento(Long sucursal, String tipoDocumento);
    Optional<Folio> findFirstBySucursalAndTipoDocumentoAndActivoTrueAndFoliosRestantesGreaterThan(
            Long sucursal, String tipoDocumento, Integer foliosRestantes);
    List<Folio> findByActivoTrue();
}
