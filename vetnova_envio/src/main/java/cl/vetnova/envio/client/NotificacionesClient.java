package cl.vetnova.envio.client;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificacionesClient {
    private static final Logger log = LoggerFactory.getLogger(NotificacionesClient.class);
    private final WebClient webClient;

    public NotificacionesClient(WebClient.Builder builder,
                                @Value("${app.notificaciones-service-url}") String notificacionesUrl) {
        this.webClient = builder.baseUrl(notificacionesUrl).build();
    }

    // El aviso al cliente es informativo: si Notificaciones está caído, el envío sigue su flujo normal.
    public void avisarCambioEstado(Long clienteId, Long envioId, String estado) {
        try {
            Map<String, Object> body = Map.of(
                    "usuarioId", clienteId,
                    "tipo", "EMAIL",
                    "mensaje", "Tu envío " + envioId + " cambió a estado " + estado
            );
            webClient.post()
                    .uri("/notificaciones")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=notificacion_envio_enviada envioId={} estado={}", envioId, estado);
        } catch (Exception ex) {
            log.warn("event=notificacion_envio_fallida envioId={} detalle={}", envioId, ex.getMessage());
        }
    }
}
