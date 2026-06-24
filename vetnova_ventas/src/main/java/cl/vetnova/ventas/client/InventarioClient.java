package cl.vetnova.ventas.client;

import cl.vetnova.ventas.exception.RemoteServiceException;
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

    public Integer consultarStock(Long idProducto, Long idSucursal) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/inventario/productos/{idProducto}/stock")
                            .queryParam("idSucursal", idSucursal)
                            .build(idProducto))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            Integer disponible = response == null ? 0 : ((Number) response.get("cantidadDisponible")).intValue();
            log.info("event=remote_consulta_stock productoId={} sucursalId={} disponible={}", idProducto, idSucursal, disponible);
            return disponible;
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo consultar stock en Inventario: " + ex.getMessage());
        }
    }

    public void registrarSalida(Long idProducto, Long idSucursal, Integer cantidad, String motivo) {
        try {
            Map<String, Object> body = Map.of(
                    "idProducto", idProducto,
                    "idSucursal", idSucursal,
                    "tipo", "SALIDA",
                    "cantidad", cantidad,
                    "motivo", motivo
            );
            webClient.post()
                    .uri("/api/v1/inventario/movimientos")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=remote_salida_stock productoId={} cantidad={}", idProducto, cantidad);
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo descontar stock en Inventario: " + ex.getMessage());
        }
    }

    public void registrarEntrada(Long idProducto, Long idSucursal, Integer cantidad, String motivo) {
        try {
            Map<String, Object> body = Map.of(
                    "idProducto", idProducto,
                    "idSucursal", idSucursal,
                    "tipo", "ENTRADA",
                    "cantidad", cantidad,
                    "motivo", motivo
            );
            webClient.post()
                    .uri("/api/v1/inventario/movimientos")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=remote_entrada_stock productoId={} cantidad={}", idProducto, cantidad);
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo reponer stock en Inventario: " + ex.getMessage());
        }
    }
}
