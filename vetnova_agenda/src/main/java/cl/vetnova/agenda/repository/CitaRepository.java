package cl.vetnova.agenda.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.agenda.model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByVeterinarioIdAndEstado(Long veterinarioId, String estado);

    boolean existsByVeterinarioIdAndFechaHoraBetween(Long veterinarioId, LocalDateTime desde, LocalDateTime hasta);

    boolean existsByVeterinarioIdAndFechaHoraAfter(Long veterinarioId, LocalDateTime fecha);

    boolean existsByVeterinarioIdAndEstadoAndFechaHoraBetween(Long veterinarioId, String estado,
            LocalDateTime desde, LocalDateTime hasta);
}
