package com.webclient.practica2.repository;

import com.webclient.practica2.model.Entrenador;
import com.webclient.practica2.model.EstadoEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {

    List<Entrenador> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    List<Entrenador> findByEspecialidadContainingIgnoreCase(String especialidad);

    List<Entrenador> findByEstado(EstadoEntrenador estado);

    boolean existsByEmail(String email);

    @Query("SELECT e FROM Entrenador e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(e.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:especialidad IS NULL OR LOWER(e.especialidad) LIKE LOWER(CONCAT('%', :especialidad, '%'))) AND " +
           "(:estado IS NULL OR e.estado = :estado)")
    List<Entrenador> buscarPorFiltros(
            @Param("nombre") String nombre,
            @Param("especialidad") String especialidad,
            @Param("estado") EstadoEntrenador estado
    );
}

