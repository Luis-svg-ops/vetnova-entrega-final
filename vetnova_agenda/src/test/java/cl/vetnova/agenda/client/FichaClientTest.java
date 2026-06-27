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

public class FichaClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FichaClient fichaClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---- obtenerNombreMascota ----

    @Test
    void testObtenerNombreMascotaNullIdRetornaNull() {
        assertNull(fichaClient.obtenerNombreMascota(null));
        verifyNoInteractions(restTemplate);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerNombreMascotaRetornaNombre() {
        Map<String, Object> body = Map.of("nombre", "Firulais");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(body));

        assertEquals("Firulais", fichaClient.obtenerNombreMascota(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerNombreMascotaResponseNullRetornaNull() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertNull(fichaClient.obtenerNombreMascota(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerNombreMascotaCuandoFichaCaeDegradaSuave() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("ficha no disponible"));

        assertNull(fichaClient.obtenerNombreMascota(1L));
    }

    // ---- verificarMascota ----

    @Test
    void testVerificarMascotaExistenteNoLanzaExcepcion() {
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());

        assertDoesNotThrow(() -> fichaClient.verificarMascota(1L));
    }

    @Test
    void testVerificarMascotaFichaCaidaLanzaResourceNotFound() {
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
                .thenThrow(new RuntimeException("timeout"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> fichaClient.verificarMascota(1L));
        assertEquals("Mascota no encontrada en el sistema", ex.getMessage());
    }
}
