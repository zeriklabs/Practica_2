package com.webclient.practica2.repository;

import com.webclient.practica2.model.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {
    List<Atleta> findByEntrenadorId(Long entrenadorId);
}

