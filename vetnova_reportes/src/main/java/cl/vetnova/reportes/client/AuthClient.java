package cl.vetnova.reportes.client;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);
    private final RestTemplate restTemplate;

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean usuarioExiste(Long usuarioId) {
        try {
            String url = "http://localhost:8081/api/usuarios/" + usuarioId + "/existe";
            Map response = restTemplate.getForObject(url, Map.class);
            boolean existe = response != null && Boolean.TRUE.equals(response.get("existe"));
            if (!existe) {
                log.warn("event=auth_usuario_no_encontrado usuarioId={}", usuarioId);
            }
            return existe;
        } catch (Exception e) {
            log.warn("event=auth_no_disponible usuarioId={} detalle={}", usuarioId, e.getMessage());
            throw new RuntimeException("No se puede generar el reporte: servicio de autenticación no disponible");
        }
    }
}

