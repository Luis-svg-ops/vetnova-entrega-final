package cl.vetnova.laboratorio.client;

import cl.vetnova.laboratorio.exception.RemoteServiceException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class FichaClient {

    private static final Logger log = LoggerFactory.getLogger(FichaClient.class);
    private final WebClient webClient;

    public FichaClient(WebClient.Builder builder, @Value("${app.ficha-service-url}") String fichaServiceUrl) {
        this.webClient = builder.baseUrl(fichaServiceUrl).build();
    }

    public String obtenerNombreMascota(Long mascotaId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/v1/mascotas/{id}", mascotaId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response == null) return null;
            return (String) response.get("nombre");
        } catch (Exception ex) {
            log.warn("event=remote_ficha_nombre_mascota_fail mascotaId={} error={}", mascotaId, ex.getMessage());
            return null;
        }
    }

    public void validarMascotaActiva(Long mascotaId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/v1/mascotas/{id}", mascotaId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response == null) {
                throw new ResourceNotFoundException("Mascota no encontrada en Ficha Clínica (id=" + mascotaId + ")");
            }
            Object activo = response.get("activo");
            if (Boolean.FALSE.equals(activo)) {
                throw new ResourceNotFoundException("La mascota (id=" + mascotaId + ") está desactivada y no puede recibir exámenes");
            }
            log.info("event=remote_ficha_mascota_ok mascotaId={}", mascotaId);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Mascota no encontrada en Ficha Clínica (id=" + mascotaId + ")");
        } catch (Exception ex) {
            throw new RemoteServiceException("Error comunicando con Ficha Clínica: " + ex.getMessage());
        }
    }
}
