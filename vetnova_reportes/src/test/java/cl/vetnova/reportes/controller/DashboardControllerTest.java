package cl.vetnova.reportes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.reportes.model.Dashboard;
import cl.vetnova.reportes.service.DashboardService;

@WebMvcTest(DashboardController.class)
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService service;

    @Test
    void testObtener() throws Exception {
        when(service.cargarIndicadores("CHILLAN")).thenReturn(new Dashboard());
        mockMvc.perform(get("/dashboard").param("sucursal", "CHILLAN")).andExpect(status().isOk());
    }
}
