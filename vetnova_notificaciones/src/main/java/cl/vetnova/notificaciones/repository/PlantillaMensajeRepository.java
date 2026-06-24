package cl.vetnova.notificaciones.repository;

import cl.vetnova.notificaciones.model.PlantillaMensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantillaMensajeRepository extends JpaRepository<PlantillaMensaje, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
