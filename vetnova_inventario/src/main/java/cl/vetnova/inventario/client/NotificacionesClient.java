package cl.vetnova.inventario.client;

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

    // La alerta es informativa: si Notificaciones no responde, el movimiento de stock no debe fallar.
    public void alertarStockCritico(Long idProducto, String idSucursal, Integer cantidad) {
        try {
            Map<String, Object> body = Map.of(
                    "usuarioId", 1L,
                    "tipo", "EMAIL",
                    "mensaje", "El producto " + idProducto + " quedó con stock " + cantidad
                            + " en la sucursal " + idSucursal
            );
            webClient.post()
                    .uri("/notificaciones")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=alerta_stock_critico_enviada productoId={} sucursalId={}", idProducto, idSucursal);
        } catch (Exception ex) {
            log.warn("event=alerta_stock_critico_fallida productoId={} detalle={}", idProducto, ex.getMessage());
        }
    }
}
