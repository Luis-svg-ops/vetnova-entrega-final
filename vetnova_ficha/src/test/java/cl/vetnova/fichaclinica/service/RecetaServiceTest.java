package cl.vetnova.fichaclinica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.fichaclinica.dto.MedicamentoRequest;
import cl.vetnova.fichaclinica.dto.RecetaRequest;
import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Receta;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.RecetaRepository;

public class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;
    @Mock
    private FichaClinicaRepository fichaClinicaRepository;
    @InjectMocks
    private RecetaService recetaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MedicamentoRequest med(String nombre, String dosis, String frecuencia) {
        MedicamentoRequest m = new MedicamentoRequest();
        m.setNombre(nombre);
        m.setDosis(dosis);
        m.setFrecuencia(frecuencia);
        return m;
    }

    private RecetaRequest request(Long fichaId, Long vetId, List<MedicamentoRequest> meds) {
        RecetaRequest r = new RecetaRequest();
        r.setFichaId(fichaId);
        r.setVeterinarioId(vetId);
        r.setMedicamentos(meds);
        return r;
    }

    private List<MedicamentoRequest> unMed() {
        List<MedicamentoRequest> lista = new ArrayList<>();
        lista.add(med("Amoxicilina", "1 comprimido", "cada 8h"));
        return lista;
    }

    private void fichaExiste() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    void testCrearFichaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recetaService.crear(request(null, 2L, unMed())));
        assertEquals("El fichaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFichaInexistente() {
        when(fichaClinicaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> recetaService.crear(request(999L, 2L, unMed())));
        assertEquals("Ficha clínica no encontrada", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recetaService.crear(request(1L, null, unMed())));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMedicamentosNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recetaService.crear(request(1L, 2L, null)));
        assertEquals("La lista de medicamentos es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearMedicamentosVacios() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recetaService.crear(request(1L, 2L, new ArrayList<>())));
        assertEquals("La receta debe tener al menos un medicamento", ex.getMessage());
    }

    @Test
    void testCrearMedicamentoSinNombre() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recetaService.crear(request(1L, 2L, List.of(med(null, "1", "8h")))));
        assertEquals("El nombre del medicamento es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMedicamentoSinDosis() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recetaService.crear(request(1L, 2L, List.of(med("Amox", null, "8h")))));
        assertEquals("La dosis es obligatoria para cada medicamento", ex.getMessage());
    }

    @Test
    void testCrearMedicamentoSinFrecuencia() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> recetaService.crear(request(1L, 2L, List.of(med("Amox", "1", null)))));
        assertEquals("La frecuencia es obligatoria para cada medicamento", ex.getMessage());
    }

    @Test
    void testCrearFechaVencimientoAnterior() {
        fichaExiste();
        RecetaRequest r = request(1L, 2L, unMed());
        r.setFechaVencimiento(Date.valueOf("2020-01-01"));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> recetaService.crear(r));
        assertEquals("La fecha de vencimiento debe ser posterior a la fecha de emisión", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizSinVencimiento() {
        fichaExiste();
        when(recetaRepository.save(any(Receta.class))).thenAnswer(inv -> inv.getArgument(0));
        Receta creada = recetaService.crear(request(1L, 2L, unMed()));
        assertEquals("Amoxicilina", creada.getMedicamentos());
        assertNotNull(creada.getFechaEmision());
    }

    @Test
    void testCrearCasoFelizConVencimientoPosterior() {
        fichaExiste();
        when(recetaRepository.save(any(Receta.class))).thenAnswer(inv -> inv.getArgument(0));
        RecetaRequest r = request(1L, 2L, unMed());
        r.setFechaVencimiento(Date.valueOf("2099-01-01"));
        assertNotNull(recetaService.crear(r).getFechaVencimiento());
    }

    @Test
    void testListarPorFicha() {
        when(recetaRepository.findByFichaIdOrderByFechaEmisionDesc(1L)).thenReturn(List.of(new Receta()));
        assertEquals(1, recetaService.listarPorFicha(1L).size());
    }

    @Test
    void testListar() {
        when(recetaRepository.findAll()).thenReturn(List.of(new Receta()));
        assertEquals(1, recetaService.listar().size());
    }
}
