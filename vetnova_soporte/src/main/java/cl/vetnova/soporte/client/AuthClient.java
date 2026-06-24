package cl.vetnova.soporte.client;

import cl.vetnova.soporte.exception.RemoteServiceException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {
    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);
    private final WebClient webClient;

    public AuthClient(WebClient.Builder builder, @Value("${app.auth-service-url}") String authServiceUrl) {
        this.webClient = builder.baseUrl(authServiceUrl).build();
    }

    public boolean usuarioExiste(Long usuarioId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/usuarios/{id}/existe", usuarioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(ex -> Mono.error(new RemoteServiceException("No se pudo validar usuario en Auth: " + ex.getMessage())))
                    .block();
            boolean existe = response != null && Boolean.TRUE.equals(response.get("existe"));
            log.info("event=remote_auth_user_exists usuarioId={} existe={}", usuarioId, existe);
            return existe;
        } catch (RemoteServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RemoteServiceException("Error comunicando con Auth Service: " + ex.getMessage());
        }
    }

    public String obtenerRol(Long usuarioId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/usuarios/{id}/rol", usuarioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response == null ? null : String.valueOf(response.get("rol"));
        } catch (Exception ex) {
            throw new RemoteServiceException("Error obteniendo rol remoto desde Auth Service: " + ex.getMessage());
        }
    }
}
