package cl.vetnova.agenda.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cl.vetnova.agenda.exception.ResourceNotFoundException;

/**
 * Cliente HTTP que comunica vetnova_agenda (8086) con vetnova_ficha (8087).
 * Igual que AuthClient, distingue entre obtención de datos (degradación suave) y
 * verificación de existencia (validación dura que bloquea la creación de la cita).
 */
@Component
public class FichaClient {

    private static final Logger log = LoggerFactory.getLogger(FichaClient.class);

    @Autowired
    private RestTemplate restTemplate;

    // URL base de vetnova_ficha; configurable desde application.properties
    @Value("${app.ficha-service-url:http://localhost:8087}")
    private String fichaUrl;

    /**
     * Obtiene el nombre de una mascota desde vetnova_ficha.
     * Degradación suave: si ficha no está disponible o la mascota no existe, retorna null sin lanzar excepción.
     * @param mascotaId id de la mascota
     * @return nombre de la mascota o null si no se pudo obtener
     */
    public String obtenerNombreMascota(Long mascotaId) {
        if (mascotaId == null) return null;
        try {
            Map<String, Object> mascota = restTemplate.exchange(
                    fichaUrl + "/api/v1/mascotas/" + mascotaId,
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
            if (mascota == null) return null;
            return (String) mascota.get("nombre");
        } catch (Exception e) {
            log.warn("event=ficha_no_disponible mascotaId={} — nombre no obtenido: {}", mascotaId, e.getMessage());
            return null;
        }
    }

    /**
     * Verifica que la mascota exista en vetnova_ficha antes de crear una cita (validación dura).
     * Si ficha no responde o la mascota no existe, lanza ResourceNotFoundException y la cita NO se crea.
     * @param mascotaId id de la mascota a verificar
     */
    public void verificarMascota(Long mascotaId) {
        try {
            restTemplate.getForObject(fichaUrl + "/api/v1/mascotas/" + mascotaId, Object.class);
        } catch (Exception e) {
            // Cualquier error (404, timeout, conexión rechazada) se traduce en mascota no encontrada
            throw new ResourceNotFoundException("Mascota no encontrada en el sistema");
        }
    }
}
