package cl.vetnova.catalogo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Buscador del catálogo (clase del modelo). Expone las operaciones de búsqueda
 * y filtrado sobre productos y servicios descritas en el diagrama de clases.
 */
public class CatalogoBuscador {

    private List<Producto> productos = new ArrayList<>();
    private List<Servicio> servicios = new ArrayList<>();

    public CatalogoBuscador() {
    }

    public CatalogoBuscador(List<Producto> productos, List<Servicio> servicios) {
        this.productos = productos;
        this.servicios = servicios;
    }

    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getNombre() != null && nombre != null
                    && p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Producto> filtrarPorCategoria(Long categoriaId) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (categoriaId != null && categoriaId.equals(p.getCategoriaId())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Producto> filtrarPorRango(Double min, Double max) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getPrecio() != null && p.getPrecio() >= min && p.getPrecio() <= max) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Producto> listarDisponibles(String sucursal) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (Boolean.TRUE.equals(p.getActivo())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Object getDetalle(Long itemId, String tipo) {
        if ("SERVICIO".equalsIgnoreCase(tipo)) {
            for (Servicio s : servicios) {
                if (itemId != null && itemId.equals(s.getId())) {
                    return s;
                }
            }
            return null;
        }
        for (Producto p : productos) {
            if (itemId != null && itemId.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    public List<Servicio> getServicios() { return servicios; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }
}
