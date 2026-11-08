package com.webclient.practica2.service;

import com.webclient.practica2.dto.EntrenadorRequest;
import com.webclient.practica2.exception.BadRequestException;
import com.webclient.practica2.exception.ResourceNotFoundException;
import com.webclient.practica2.model.Entrenador;
import com.webclient.practica2.model.EstadoEntrenador;
import com.webclient.practica2.repository.EntrenadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntrenadorService {

    private final EntrenadorRepository entrenadorRepository;

    public EntrenadorService(EntrenadorRepository entrenadorRepository) {
        this.entrenadorRepository = entrenadorRepository;
    }

    @Transactional(readOnly = true)
    public List<Entrenador> listarTodos() {
        return entrenadorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Entrenador obtenerPorId(Long id) {
        return entrenadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el entrenador con ID: " + id));
    }

    @Transactional
    public Entrenador crear(EntrenadorRequest request) {
        if (entrenadorRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un entrenador registrado con el correo: " + request.getEmail());
        }

        Entrenador entrenador = new Entrenador();
        entrenador.setNombre(request.getNombre());
        entrenador.setApellido(request.getApellido());
        entrenador.setEspecialidad(request.getEspecialidad());
        entrenador.setExperienciaAnios(request.getExperienciaAnios());
        entrenador.setEmail(request.getEmail());
        entrenador.setTelefono(request.getTelefono());
        entrenador.setEstado(request.getEstado() != null ? request.getEstado() : EstadoEntrenador.ACTIVO);

        return entrenadorRepository.save(entrenador);
    }

    @Transactional
    public Entrenador actualizar(Long id, EntrenadorRequest request) {
        Entrenador entrenador = obtenerPorId(id);

        if (!entrenador.getEmail().equalsIgnoreCase(request.getEmail()) &&
                entrenadorRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El correo " + request.getEmail() + " ya está asignado a otro entrenador");
        }

        entrenador.setNombre(request.getNombre());
        entrenador.setApellido(request.getApellido());
        entrenador.setEspecialidad(request.getEspecialidad());
        entrenador.setExperienciaAnios(request.getExperienciaAnios());
        entrenador.setEmail(request.getEmail());
        entrenador.setTelefono(request.getTelefono());
        if (request.getEstado() != null) {
            entrenador.setEstado(request.getEstado());
        }

        return entrenadorRepository.save(entrenador);
    }

    @Transactional
    public void eliminar(Long id) {
        Entrenador entrenador = obtenerPorId(id);
        entrenadorRepository.delete(entrenador);
    }

    @Transactional
    public Entrenador cambiarEstado(Long id, EstadoEntrenador nuevoEstado) {
        Entrenador entrenador = obtenerPorId(id);
        entrenador.setEstado(nuevoEstado);
        return entrenadorRepository.save(entrenador);
    }

    @Transactional(readOnly = true)
    public List<Entrenador> buscar(String nombre, String especialidad, EstadoEntrenador estado) {
        if (nombre == null && especialidad == null && estado == null) {
            return entrenadorRepository.findAll();
        }
        return entrenadorRepository.buscarPorFiltros(nombre, especialidad, estado);
    }
}

