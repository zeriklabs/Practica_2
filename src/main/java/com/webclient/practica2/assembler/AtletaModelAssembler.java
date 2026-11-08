package com.webclient.practica2.assembler;

import com.webclient.practica2.controller.AtletaController;
import com.webclient.practica2.controller.EntrenadorController;
import com.webclient.practica2.dto.AtletaResponse;
import com.webclient.practica2.model.Atleta;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AtletaModelAssembler extends RepresentationModelAssemblerSupport<Atleta, AtletaResponse> {

    public AtletaModelAssembler() {
        super(AtletaController.class, AtletaResponse.class);
    }

    @Override
    public AtletaResponse toModel(Atleta entity) {
        AtletaResponse model = new AtletaResponse();
        model.setId(entity.getId());
        model.setNombre(entity.getNombre());
        model.setApellido(entity.getApellido());
        model.setEdad(entity.getEdad());
        model.setCategoria(entity.getCategoria());

        if (entity.getEntrenador() != null) {
            Long entrenadorId = entity.getEntrenador().getId();
            model.setEntrenadorId(entrenadorId);
            model.setEntrenadorNombre(entity.getEntrenador().getNombre() + " " + entity.getEntrenador().getApellido());

            // Enlace al entrenador asignado
            model.add(linkTo(methodOn(EntrenadorController.class).obtenerPorId(entrenadorId)).withRel("entrenador"));

            // Enlace a la lista de atletas de este entrenador
            model.add(linkTo(methodOn(AtletaController.class).obtenerAtletasPorEntrenador(entrenadorId)).withRel("atletas-del-entrenador"));
        }

        // Enlace self
        model.add(linkTo(methodOn(AtletaController.class).obtenerAtletaPorId(entity.getId())).withSelfRel());

        return model;
    }
}

