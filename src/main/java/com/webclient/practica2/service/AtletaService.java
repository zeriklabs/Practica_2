package com.webclient.practica2.service;

import com.webclient.practica2.dto.AtletaRequest;
import com.webclient.practica2.exception.BadRequestException;
import com.webclient.practica2.exception.ResourceNotFoundException;
import com.webclient.practica2.model.Atleta;
import com.webclient.practica2.model.Entrenador;
import com.webclient.practica2.model.EstadoEntrenador;
import com.webclient.practica2.repository.AtletaRepository;
import com.webclient.practica2.repository.EntrenadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AtletaService {

    private final AtletaRepository atletaRepository;
    private final EntrenadorRepository entrenadorRepository;

    public AtletaService(AtletaRepository atletaRepository, EntrenadorRepository entrenadorRepository) {
        this.atletaRepository = atletaRepository;
        this.entrenadorRepository = entrenadorRepository;
    }

    @Transactional(readOnly = true)
    public List<Atleta> listarPorEntrenador(Long entrenadorId) {
        if (!entrenadorRepository.existsById(entrenadorId)) {
            throw new ResourceNotFoundException("No se encontró el entrenador con ID: " + entrenadorId);
        }
        return atletaRepository.findByEntrenadorId(entrenadorId);
    }

    @Transactional(readOnly = true)
    public Atleta obtenerPorId(Long id) {
        return atletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el atleta con ID: " + id));
    }

    @Transactional
    public Atleta asignarAEntrenador(Long entrenadorId, AtletaRequest request) {
        Entrenador entrenador = entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el entrenador con ID: " + entrenadorId));

        if (entrenador.getEstado() != EstadoEntrenador.ACTIVO) {
            throw new BadRequestException("No es posible asignar atletas a un entrenador que no esté en estado ACTIVO (Estado actual: " + entrenador.getEstado() + ")");
        }

        Atleta atleta = new Atleta();
        atleta.setNombre(request.getNombre());
        atleta.setApellido(request.getApellido());
        atleta.setEdad(request.getEdad());
        atleta.setCategoria(request.getCategoria());
        atleta.setEntrenador(entrenador);

        return atletaRepository.save(atleta);
    }

    @Transactional
    public void eliminar(Long id) {
        Atleta atleta = obtenerPorId(id);
        atletaRepository.delete(atleta);
    }
}

