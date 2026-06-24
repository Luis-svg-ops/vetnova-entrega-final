package cl.vetnova.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.catalogo.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByCategoriaId(Long categoriaId);
}