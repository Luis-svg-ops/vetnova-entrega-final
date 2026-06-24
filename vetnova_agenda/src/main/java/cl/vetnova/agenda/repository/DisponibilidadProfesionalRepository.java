package cl.vetnova.agenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.agenda.model.DisponibilidadProfesional;

@Repository
public interface DisponibilidadProfesionalRepository extends JpaRepository<DisponibilidadProfesional, Long> {

    boolean existsByVeterinarioIdAndDiaSemanaAndSucursal(Long veterinarioId, String diaSemana, String sucursal);
}
