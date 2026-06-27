package cl.vetnova.notificaciones;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class NotificacionesApplicationTest {

    @Disabled("Requiere servicios externos corriendo")
    @Test
    void testMainLevantaLaAplicacion() {
        assertDoesNotThrow(() -> NotificacionesApplication.main(new String[] {
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:notificacionescovdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
        }));
    }
}
