package cl.vetnova.envio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.envio.model.RutaDespacho;

@Repository
public interface RutaDespachoRepository extends JpaRepository<RutaDespacho, Long> {

    boolean existsBySucursalOrigenAndSucursalDestino(String sucursalOrigen, String sucursalDestino);

    List<RutaDespacho> findBySucursalOrigenAndSucursalDestinoAndActivaTrue(String sucursalOrigen, String sucursalDestino);
}
