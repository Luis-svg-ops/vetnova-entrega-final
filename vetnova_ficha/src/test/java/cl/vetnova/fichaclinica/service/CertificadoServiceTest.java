package cl.vetnova.fichaclinica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Certificado;
import cl.vetnova.fichaclinica.repository.CertificadoRepository;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.VacunaRepository;

public class CertificadoServiceTest {

    @Mock
    private CertificadoRepository certificadoRepository;
    @Mock
    private FichaClinicaRepository fichaClinicaRepository;
    @Mock
    private VacunaRepository vacunaRepository;
    @InjectMocks
    private CertificadoService certificadoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Certificado cert(Long fichaId, Long vetId, String tipo) {
        Certificado c = new Certificado();
        c.setFichaId(fichaId);
        c.setVeterinarioId(vetId);
        c.setTipo(tipo);
        return c;
    }

    private void fichaExiste() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    void testCrearFichaIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> certificadoService.crear(cert(null, 2L, "SALUD")));
        assertEquals("El fichaId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFichaInexistente() {
        when(fichaClinicaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> certificadoService.crear(cert(999L, 2L, "SALUD")));
        assertEquals("Ficha clínica no encontrada", ex.getMessage());
    }

    @Test
    void testCrearVeterinarioNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> certificadoService.crear(cert(1L, null, "SALUD")));
        assertEquals("El veterinarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> certificadoService.crear(cert(1L, 2L, null)));
        assertEquals("El tipo de certificado es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        fichaExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> certificadoService.crear(cert(1L, 2L, "OTRO")));
        assertEquals("Tipo no válido. Valores permitidos: SALUD, VACUNACION, VIAJE, ADOPCION", ex.getMessage());
    }

    @Test
    void testCrearVacunacionSinVacunas() {
        fichaExiste();
        when(vacunaRepository.existsByFichaId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> certificadoService.crear(cert(1L, 2L, "VACUNACION")));
        assertEquals("No se puede emitir certificado de vacunación sin vacunas registradas en la ficha",
                ex.getMessage());
    }

    @Test
    void testCrearVacunacionConVacunas() {
        fichaExiste();
        when(vacunaRepository.existsByFichaId(1L)).thenReturn(true);
        when(certificadoRepository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));
        Certificado creado = certificadoService.crear(cert(1L, 2L, "VACUNACION"));
        assertNotNull(creado.getFechaEmision());
    }

    @Test
    void testCrearFechaVencimientoAnterior() {
        fichaExiste();
        Certificado c = cert(1L, 2L, "SALUD");
        c.setFechaVencimiento(Date.valueOf("2020-01-01"));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> certificadoService.crear(c));
        assertEquals("La fecha de vencimiento debe ser posterior a la fecha de emisión", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizSinVencimiento() {
        fichaExiste();
        when(certificadoRepository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));
        Certificado creado = certificadoService.crear(cert(1L, 2L, "SALUD"));
        assertEquals(Date.valueOf(LocalDate.now()), creado.getFechaEmision());
    }

    @Test
    void testCrearCasoFelizConVencimientoPosterior() {
        fichaExiste();
        when(certificadoRepository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));
        Certificado c = cert(1L, 2L, "VIAJE");
        c.setFechaVencimiento(Date.valueOf("2099-01-01"));
        assertNotNull(certificadoService.crear(c).getFechaVencimiento());
    }

    @Test
    void testListarPorFicha() {
        when(certificadoRepository.findByFichaId(1L)).thenReturn(List.of(new Certificado()));
        assertEquals(1, certificadoService.listarPorFicha(1L).size());
    }

    @Test
    void testListar() {
        when(certificadoRepository.findAll()).thenReturn(List.of(new Certificado()));
        assertEquals(1, certificadoService.listar().size());
    }
}
