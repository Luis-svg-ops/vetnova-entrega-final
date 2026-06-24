package cl.vetnova.soporte.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.soporte.client.AuthClient;
import cl.vetnova.soporte.dto.CerrarTicketRequest;
import cl.vetnova.soporte.dto.ClasificarTicketRequest;
import cl.vetnova.soporte.dto.CrearTicketRequest;
import cl.vetnova.soporte.dto.DerivarTicketRequest;
import cl.vetnova.soporte.dto.ResponderTicketRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.CategoriaTicket;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.repository.CategoriaTicketRepository;
import cl.vetnova.soporte.repository.DerivacionTicketRepository;
import cl.vetnova.soporte.repository.EscalamientoTicketRepository;
import cl.vetnova.soporte.repository.RespuestaTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;

public class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private CategoriaTicketRepository categoriaRepository;
    @Mock private DerivacionTicketRepository derivacionRepository;
    @Mock private EscalamientoTicketRepository escalamientoRepository;
    @Mock private RespuestaTicketRepository respuestaRepository;
    @Mock private AuthClient authClient;
    @InjectMocks private TicketService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CrearTicketRequest crearReq(Long clienteId, String motivo, String descripcion) {
        CrearTicketRequest r = new CrearTicketRequest();
        r.setClienteId(clienteId);
        r.setMotivo(motivo);
        r.setDescripcion(descripcion);
        return r;
    }

    private Ticket ticket(Long id, String estado) {
        Ticket t = new Ticket();
        t.setId(id);
        t.setEstado(estado);
        return t;
    }

    private ClasificarTicketRequest clasReq(Long categoriaId, String prioridad) {
        ClasificarTicketRequest r = new ClasificarTicketRequest();
        r.setCategoriaId(categoriaId);
        r.setPrioridad(prioridad);
        return r;
    }

    private DerivarTicketRequest derReq(Long responsableId) {
        DerivarTicketRequest r = new DerivarTicketRequest();
        r.setResponsableId(responsableId);
        return r;
    }

    private ResponderTicketRequest respReq(Long autorId, String contenido, Boolean visible) {
        ResponderTicketRequest r = new ResponderTicketRequest();
        r.setAutorId(autorId);
        r.setContenido(contenido);
        r.setVisible(visible);
        return r;
    }

    private CerrarTicketRequest cerrarReq(String resolucion) {
        CerrarTicketRequest r = new CerrarTicketRequest();
        r.setResolucion(resolucion);
        return r;
    }

    // ---------- crear ----------
    @Test
    void testCrearClienteIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(null, "m", "d")));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearClienteInexistente() {
        when(authClient.usuarioExiste(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(crearReq(999L, "m", "d")));
        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    @Test
    void testCrearMotivoNull() {
        when(authClient.usuarioExiste(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(1L, null, "d")));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMotivoVacio() {
        when(authClient.usuarioExiste(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(1L, "  ", "d")));
        assertEquals("El motivo no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearDescripcionNull() {
        when(authClient.usuarioExiste(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(1L, "m", null)));
        assertEquals("La descripción es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearDescripcionVacia() {
        when(authClient.usuarioExiste(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(crearReq(1L, "m", "  ")));
        assertEquals("La descripción no puede estar vacía", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(authClient.usuarioExiste(1L)).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        Ticket creado = service.crear(crearReq(1L, "Producto defectuoso", "El producto llegó roto"));
        assertEquals("ABIERTO", creado.getEstado());
        assertNotNull(creado.getFechaCreacion());
    }

    // ---------- clasificar ----------
    @Test
    void testClasificarTicketInexistente() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.clasificar(99L, clasReq(1L, "ALTA")));
        assertEquals("Ticket no encontrado", ex.getMessage());
    }

    @Test
    void testClasificarCategoriaInexistente() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "ABIERTO")));
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.clasificar(1L, clasReq(999L, "ALTA")));
        assertEquals("Categoría no encontrada", ex.getMessage());
    }

    @Test
    void testClasificarCategoriaIdNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "ABIERTO")));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.clasificar(1L, clasReq(null, "ALTA")));
        assertEquals("Categoría no encontrada", ex.getMessage());
    }

    @Test
    void testClasificarPrioridadInvalida() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "ABIERTO")));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(new CategoriaTicket()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.clasificar(1L, clasReq(1L, "URGENTISIMA")));
        assertEquals("Prioridad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA", ex.getMessage());
    }

    @Test
    void testClasificarPrioridadNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "ABIERTO")));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(new CategoriaTicket()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.clasificar(1L, clasReq(1L, null)));
        assertEquals("Prioridad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA", ex.getMessage());
    }

    @Test
    void testClasificarCasoFeliz() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "ABIERTO")));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(new CategoriaTicket()));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        Ticket clasificado = service.clasificar(1L, clasReq(1L, "ALTA"));
        assertEquals("CLASIFICADO", clasificado.getEstado());
        assertEquals("ALTA", clasificado.getPrioridad());
    }

    // ---------- derivar ----------
    @Test
    void testDerivarTicketInexistente() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.derivar(99L, derReq(3L)));
    }

    @Test
    void testDerivarResponsableInexistente() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO")));
        when(authClient.usuarioExiste(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.derivar(1L, derReq(999L)));
        assertEquals("Responsable no encontrado", ex.getMessage());
    }

    @Test
    void testDerivarTicketCerrado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.derivar(1L, derReq(3L)));
        assertEquals("No se puede derivar un ticket cerrado", ex.getMessage());
    }

    @Test
    void testDerivarCasoFeliz() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        Ticket derivado = service.derivar(1L, derReq(3L));
        assertEquals("DERIVADO", derivado.getEstado());
        assertEquals(3L, derivado.getResponsableId());
        verify(derivacionRepository).save(any());
    }

    // ---------- escalar ----------
    @Test
    void testEscalarTicketInexistente() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.escalar(99L));
    }

    @Test
    void testEscalarYaEscalado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "ESCALADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.escalar(1L));
        assertEquals("El ticket ya está escalado", ex.getMessage());
    }

    @Test
    void testEscalarCasoFeliz() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        Ticket escalado = service.escalar(1L);
        assertEquals("ESCALADO", escalado.getEstado());
        verify(escalamientoRepository).save(any());
    }

    // ---------- responder ----------
    @Test
    void testResponderTicketInexistente() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.responder(99L, respReq(3L, "hola", null)));
    }

    @Test
    void testResponderContenidoNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.responder(1L, respReq(3L, null, null)));
        assertEquals("El contenido de la respuesta es obligatorio", ex.getMessage());
    }

    @Test
    void testResponderTicketCerrado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.responder(1L, respReq(3L, "Respuesta", null)));
        assertEquals("No se puede responder un ticket cerrado", ex.getMessage());
    }

    @Test
    void testResponderCasoFelizVisiblePorDefecto() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(respuestaRepository.save(any(RespuestaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        RespuestaTicket r = service.responder(1L, respReq(3L, "Hemos revisado", null));
        assertTrue(r.getVisible());
    }

    @Test
    void testResponderCasoFelizVisibleFalse() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(respuestaRepository.save(any(RespuestaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        RespuestaTicket r = service.responder(1L, respReq(3L, "Nota interna", false));
        assertFalse(r.getVisible());
    }

    @Test
    void testResponderCasoFelizVisibleTrue() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(respuestaRepository.save(any(RespuestaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        RespuestaTicket r = service.responder(1L, respReq(3L, "Respuesta visible", true));
        assertTrue(r.getVisible());
    }

    // ---------- cerrar ----------
    @Test
    void testCerrarTicketInexistente() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.cerrar(99L, cerrarReq("ok")));
    }

    @Test
    void testCerrarSinResolucionNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cerrar(1L, cerrarReq(null)));
        assertEquals("La resolución es obligatoria para cerrar el ticket", ex.getMessage());
    }

    @Test
    void testCerrarSinResolucionVacia() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cerrar(1L, cerrarReq("  ")));
        assertEquals("La resolución es obligatoria para cerrar el ticket", ex.getMessage());
    }

    @Test
    void testCerrarCasoFeliz() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        Ticket cerrado = service.cerrar(1L, cerrarReq("Reembolso procesado"));
        assertEquals("CERRADO", cerrado.getEstado());
        assertEquals("Reembolso procesado", cerrado.getResolucion());
        assertNotNull(cerrado.getFechaCierre());
    }

    // ---------- listar ----------
    @Test
    void testListarSinFiltro() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticket(1L, "ABIERTO")));
        assertEquals(1, service.listar(null).size());
    }

    @Test
    void testListarPorEstado() {
        when(ticketRepository.findByEstadoIgnoreCase("ABIERTO")).thenReturn(List.of(ticket(1L, "ABIERTO")));
        assertEquals(1, service.listar("ABIERTO").size());
    }
}
