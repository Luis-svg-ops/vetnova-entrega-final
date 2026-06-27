package cl.vetnova.fichaclinica;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class FichaClinica1ApplicationTest {

    @Disabled("Requiere servicios externos (auth, agenda) corriendo")
    @Test
    void testMainLevantaLaAplicacion() {
        assertDoesNotThrow(() -> FichaClinica1Application.main(new String[] {
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:fichacovdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
        }));
    }
}
