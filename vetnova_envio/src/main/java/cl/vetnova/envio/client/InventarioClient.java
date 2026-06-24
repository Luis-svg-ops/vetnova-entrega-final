package cl.vetnova.envio.client;

import cl.vetnova.envio.exception.RemoteServiceException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InventarioClient {
    private static final Logger log = LoggerFactory.getLogger(InventarioClient.class);
    private final WebClient webClient;

    public InventarioClient(WebClient.Builder builder,
                            @Value("${app.inventario-service-url}") String inventarioUrl) {
        this.webClient = builder.baseUrl(inventarioUrl).build();
    }

    public void registrarMovimiento(Long idProducto, Long idSucursal, String tipo, Integer cantidad, String motivo) {
        try {
            Map<String, Object> body = Map.of(
                    "idProducto", idProducto,
                    "idSucursal", idSucursal,
                    "tipo", tipo,
                    "cantidad", cantidad,
                    "motivo", motivo
            );
            webClient.post()
                    .uri("/api/v1/inventario/movimientos")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=remote_movimiento_inventario productoId={} tipo={} cantidad={}", idProducto, tipo, cantidad);
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo registrar el movimiento en Inventario: " + ex.getMessage());
        }
    }
}
