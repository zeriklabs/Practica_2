package com.webclient.practica2.assembler;

import com.webclient.practica2.controller.AtletaController;
import com.webclient.practica2.controller.EntrenadorController;
import com.webclient.practica2.dto.EntrenadorResponse;
import com.webclient.practica2.model.Entrenador;
import com.webclient.practica2.model.EstadoEntrenador;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EntrenadorModelAssembler extends RepresentationModelAssemblerSupport<Entrenador, EntrenadorResponse> {

    public EntrenadorModelAssembler() {
        super(EntrenadorController.class, EntrenadorResponse.class);
    }

    @Override
    public EntrenadorResponse toModel(Entrenador entity) {
        EntrenadorResponse model = new EntrenadorResponse();
        model.setId(entity.getId());
        model.setNombre(entity.getNombre());
        model.setApellido(entity.getApellido());
        model.setEspecialidad(entity.getEspecialidad());
        model.setExperienciaAnios(entity.getExperienciaAnios());
        model.setEmail(entity.getEmail());
        model.setTelefono(entity.getTelefono());
        model.setEstado(entity.getEstado());
        model.setCantidadAtletas(entity.getAtletas() != null ? entity.getAtletas().size() : 0);

        // Enlace "self": apunta al recurso consultado (/api/entrenadores/{id})
        model.add(linkTo(methodOn(EntrenadorController.class).obtenerPorId(entity.getId())).withSelfRel());

        // Enlace "entrenadores": coleccion principal (/api/entrenadores)
        model.add(linkTo(methodOn(EntrenadorController.class).obtenerTodos()).withRel("entrenadores"));

        // Enlace "actualizar": operacion PUT (/api/entrenadores/{id})
        model.add(linkTo(methodOn(EntrenadorController.class).actualizar(entity.getId(), null)).withRel("actualizar"));

        // Enlace "eliminar": operacion DELETE (/api/entrenadores/{id})
        model.add(linkTo(methodOn(EntrenadorController.class).eliminar(entity.getId())).withRel("eliminar"));

        // Enlace a atletas relacionados (/api/entrenadores/{id}/atletas)
        model.add(linkTo(methodOn(AtletaController.class).obtenerAtletasPorEntrenador(entity.getId())).withRel("atletas"));

        // Enlaces condicionales según el estado del recurso (Requisito HATEOAS Nivel 3)
        if (entity.getEstado() == EstadoEntrenador.ACTIVO) {
            model.add(linkTo(methodOn(EntrenadorController.class).cambiarEstado(entity.getId(), null))
                    .withRel("desactivar"));
            model.add(linkTo(methodOn(AtletaController.class).asignarAtleta(entity.getId(), null))
                    .withRel("asignar-atleta"));
        } else if (entity.getEstado() == EstadoEntrenador.INACTIVO) {
            model.add(linkTo(methodOn(EntrenadorController.class).cambiarEstado(entity.getId(), null))
                    .withRel("activar"));
        } else if (entity.getEstado() == EstadoEntrenador.SUSPENDIDO) {
            model.add(linkTo(methodOn(EntrenadorController.class).cambiarEstado(entity.getId(), null))
                    .withRel("reactivar"));
        }

        return model;
    }
}

