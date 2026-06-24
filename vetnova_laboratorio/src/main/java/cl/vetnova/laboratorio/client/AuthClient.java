package cl.vetnova.laboratorio.client;

import cl.vetnova.laboratorio.exception.RemoteServiceException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthClient {
    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);
    private final WebClient webClient;
    public AuthClient(WebClient.Builder builder, @Value("${app.auth-service-url}") String authServiceUrl) { this.webClient = builder.baseUrl(authServiceUrl).build(); }
    public boolean usuarioExiste(Long usuarioId) {
        try {
            Map<?, ?> response = webClient.get().uri("/api/usuarios/{id}/existe", usuarioId).retrieve().bodyToMono(Map.class).block();
            boolean existe = response != null && Boolean.TRUE.equals(response.get("existe"));
            log.info("event=remote_auth_user_exists usuarioId={} existe={}", usuarioId, existe);
            return existe;
        } catch (Exception ex) { throw new RemoteServiceException("Error comunicando con Auth Service: " + ex.getMessage()); }
    }
    public String obtenerRol(Long usuarioId) {
        try {
            Map<?, ?> response = webClient.get().uri("/api/usuarios/{id}/rol", usuarioId).retrieve().bodyToMono(Map.class).block();
            return response == null ? null : String.valueOf(response.get("rol"));
        } catch (Exception ex) { throw new RemoteServiceException("Error obteniendo rol desde Auth Service: " + ex.getMessage()); }
    }
}
