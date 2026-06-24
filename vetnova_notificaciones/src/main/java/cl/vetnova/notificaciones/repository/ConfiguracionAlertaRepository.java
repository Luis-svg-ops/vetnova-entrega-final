package cl.vetnova.notificaciones.repository;

import cl.vetnova.notificaciones.model.ConfiguracionAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfiguracionAlertaRepository extends JpaRepository<ConfiguracionAlerta, Long> {
    boolean existsByUsuarioIdAndTipoEventoAndCanal(Long usuarioId, String tipoEvento, String canal);
    List<ConfiguracionAlerta> findByUsuarioId(Long usuarioId);
}
