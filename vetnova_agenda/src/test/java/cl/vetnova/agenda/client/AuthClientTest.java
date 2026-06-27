package cl.vetnova.agenda.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cl.vetnova.agenda.exception.ResourceNotFoundException;

public class AuthClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---- obtenerNombre ----

    @Test
    void testObtenerNombreNullIdRetornaNull() {
        assertNull(authClient.obtenerNombre(null));
        verifyNoInteractions(restTemplate);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerNombreRetornaNombreApellido() {
        Map<String, Object> body = Map.of("nombre", "Juan", "apellido", "Pérez");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(body));

        assertEquals("Juan Pérez", authClient.obtenerNombre(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerNombreSoloNombreSinApellido() {
        Map<String, Object> body = Map.of("nombre", "Juan");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(body));

        String resultado = authClient.obtenerNombre(1L);
        assertTrue(resultado.contains("Juan"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerNombreResponseNullRetornaNull() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertNull(authClient.obtenerNombre(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerNombreCuandoAuthCaeDegradaSuave() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("conexión rechazada"));

        assertNull(authClient.obtenerNombre(1L));
    }

    // ---- verificarCliente ----

    @SuppressWarnings("unchecked")
    @Test
    void testVerificarClienteExistenteNoLanzaExcepcion() {
        Map<String, Object> body = Map.of("existe", true);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(body));

        assertDoesNotThrow(() -> authClient.verificarCliente(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testVerificarClienteNoExistenteLanzaResourceNotFound() {
        Map<String, Object> body = Map.of("existe", false);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(body));

        assertThrows(ResourceNotFoundException.class, () -> authClient.verificarCliente(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testVerificarClienteRespuestaNullLanzaResourceNotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(ResourceNotFoundException.class, () -> authClient.verificarCliente(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testVerificarClienteAuthCaidaLanzaResourceNotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("timeout"));

        assertThrows(ResourceNotFoundException.class, () -> authClient.verificarCliente(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testVerificarClienteResourceNotFoundSePropaga() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado en el sistema"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> authClient.verificarCliente(1L));
        assertEquals("Cliente no encontrado en el sistema", ex.getMessage());
    }
}
