package cl.vetnova.notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NotificacionesApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificacionesApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
    return new RestTemplate();
    }
    //NO VAYAN A TOCAR ESTA WEA ATTE : LUIS
}
