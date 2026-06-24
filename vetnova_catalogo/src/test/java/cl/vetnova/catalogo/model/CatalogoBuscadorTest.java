package cl.vetnova.catalogo.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CatalogoBuscadorTest {

    private Producto producto(Long id, String nombre, Long categoriaId, Double precio, Boolean activo) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setCategoriaId(categoriaId);
        p.setPrecio(precio);
        p.setActivo(activo);
        return p;
    }

    private CatalogoBuscador buscador() {
        List<Producto> productos = new ArrayList<>();
        productos.add(producto(1L, "Alimento perro", 10L, 15000.0, true));
        productos.add(producto(2L, "Juguete gato", 20L, 5000.0, false));
        productos.add(new Producto()); // producto con campos nulos (rama de nombre null)
        Servicio servicio = new Servicio();
        servicio.setId(99L);
        servicio.setNombre("Consulta");
        List<Servicio> servicios = new ArrayList<>();
        servicios.add(servicio);
        return new CatalogoBuscador(productos, servicios);
    }

    @Test
    void testBuscarPorNombreEncuentraYDescarta() {
        CatalogoBuscador b = buscador();
        assertEquals(1, b.buscarPorNombre("perro").size());
        assertTrue(b.buscarPorNombre("inexistente").isEmpty());
        assertTrue(b.buscarPorNombre(null).isEmpty());
    }

    @Test
    void testFiltrarPorCategoria() {
        CatalogoBuscador b = buscador();
        assertEquals(1, b.filtrarPorCategoria(10L).size());
        assertTrue(b.filtrarPorCategoria(null).isEmpty());
        assertTrue(b.filtrarPorCategoria(999L).isEmpty());
    }

    @Test
    void testFiltrarPorRango() {
        CatalogoBuscador b = buscador();
        assertEquals(1, b.filtrarPorRango(10000.0, 20000.0).size());
        assertEquals(2, b.filtrarPorRango(0.0, 100000.0).size());
        // precio por encima del máximo queda fuera
        assertEquals(1, b.filtrarPorRango(0.0, 6000.0).size());
    }

    @Test
    void testListarDisponibles() {
        CatalogoBuscador b = buscador();
        assertEquals(1, b.listarDisponibles("Chillán").size());
    }

    @Test
    void testGetDetalleProductoYServicio() {
        CatalogoBuscador b = buscador();
        assertNotNull(b.getDetalle(1L, "PRODUCTO"));
        assertNull(b.getDetalle(999L, "PRODUCTO"));
        assertNotNull(b.getDetalle(99L, "SERVICIO"));
        assertNull(b.getDetalle(1L, "SERVICIO"));
        // itemId nulo no encuentra nada en ninguna rama
        assertNull(b.getDetalle(null, "PRODUCTO"));
        assertNull(b.getDetalle(null, "SERVICIO"));
    }

    @Test
    void testConstructorVacioYAccesores() {
        CatalogoBuscador b = new CatalogoBuscador();
        b.setProductos(new ArrayList<>());
        b.setServicios(new ArrayList<>());
        assertNotNull(b.getProductos());
        assertNotNull(b.getServicios());
        assertTrue(b.buscarPorNombre("x").isEmpty());
    }
}
