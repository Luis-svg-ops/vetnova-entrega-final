package cl.vetnova.fichaclinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.fichaclinica.model.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    boolean existsByMicrochip(String microchip);
}
