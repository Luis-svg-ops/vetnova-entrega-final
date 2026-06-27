package cl.vetnova.ventas.client;

import cl.vetnova.ventas.exception.RemoteServiceException;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CatalogoClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogoClient.class);
    private final WebClient webClient;

    public CatalogoClient(WebClient.Builder builder,
                          @Value("${app.catalogo-service-url}") String catalogoUrl) {
        this.webClient = builder.baseUrl(catalogoUrl).build();
    }

    public void validarProductoExiste(Long productoId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/v1/productos/{id}", productoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response == null) {
                throw new ResourceNotFoundException("Producto no encontrado en Catálogo (id=" + productoId + ")");
            }
            log.info("event=remote_catalogo_producto_ok productoId={}", productoId);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Producto no encontrado en Catálogo (id=" + productoId + ")");
        } catch (Exception ex) {
            throw new RemoteServiceException("No se puede procesar la venta: servicio de catálogo no disponible");
        }
    }
}
