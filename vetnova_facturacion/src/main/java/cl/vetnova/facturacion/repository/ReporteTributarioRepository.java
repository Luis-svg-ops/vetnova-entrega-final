package cl.vetnova.facturacion.repository;

import cl.vetnova.facturacion.model.ReporteTributario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteTributarioRepository extends JpaRepository<ReporteTributario, Long> {

    boolean existsBySucursalAndPeriodo(Long sucursal, String periodo);
    List<ReporteTributario> findBySucursal(Long sucursal);
}
