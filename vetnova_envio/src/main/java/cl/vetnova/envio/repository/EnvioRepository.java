package cl.vetnova.envio.repository;

import cl.vetnova.envio.model.Envio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByOrdenId(Long ordenId);
    Optional<Envio> findByNumeroGuia(String numeroGuia);
}
