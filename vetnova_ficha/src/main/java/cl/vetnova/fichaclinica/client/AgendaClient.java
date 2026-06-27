package cl.vetnova.fichaclinica.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AgendaClient {

    private static final String AGENDA_URL = "http://localhost:8086";

    @Autowired
    private RestTemplate restTemplate;

    public Map<Object, Object> obtenerCita(Long citaId) {
        return restTemplate.exchange(
                AGENDA_URL + "/api/v1/citas/" + citaId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<Object, Object>>() {}
        ).getBody();
    }
}
