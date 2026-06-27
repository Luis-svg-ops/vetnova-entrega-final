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

@Component
public class FichaClient {

    private static final Logger log = LoggerFactory.getLogger(FichaClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.ficha-service-url:http://localhost:8087}")
    private String fichaUrl;

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

    public void verificarMascota(Long mascotaId) {
        try {
            restTemplate.getForObject(fichaUrl + "/api/v1/mascotas/" + mascotaId, Object.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Mascota no encontrada en el sistema");
        }
    }
}
