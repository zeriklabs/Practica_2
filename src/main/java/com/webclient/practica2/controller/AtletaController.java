package com.webclient.practica2.controller;

import com.webclient.practica2.assembler.AtletaModelAssembler;
import com.webclient.practica2.dto.AtletaRequest;
import com.webclient.practica2.dto.AtletaResponse;
import com.webclient.practica2.model.Atleta;
import com.webclient.practica2.service.AtletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Atletas", description = "Endpoints para la gestión y relación de atletas a cargo de entrenadores")
@SecurityRequirement(name = "BearerAuth")
public class AtletaController {

    private final AtletaService atletaService;
    private final AtletaModelAssembler assembler;

    public AtletaController(AtletaService atletaService, AtletaModelAssembler assembler) {
        this.atletaService = atletaService;
        this.assembler = assembler;
    }

    @GetMapping("/api/entrenadores/{entrenadorId}/atletas")
    @Operation(summary = "Consultar atletas de un entrenador", description = "Permite navegar de un entrenador a sus atletas asociados (HATEOAS).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de atletas obtenida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    public ResponseEntity<CollectionModel<AtletaResponse>> obtenerAtletasPorEntrenador(@PathVariable Long entrenadorId) {
        List<Atleta> atletas = atletaService.listarPorEntrenador(entrenadorId);
        CollectionModel<AtletaResponse> collectionModel = assembler.toCollectionModel(atletas);

        collectionModel.add(linkTo(methodOn(AtletaController.class).obtenerAtletasPorEntrenador(entrenadorId)).withSelfRel());
        collectionModel.add(linkTo(methodOn(EntrenadorController.class).obtenerPorId(entrenadorId)).withRel("entrenador"));

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping("/api/entrenadores/{entrenadorId}/atletas")
    @Operation(summary = "Asignar un atleta a un entrenador", description = "Requiere rol ADMIN. Solo se permite si el entrenador está en estado ACTIVO.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Atleta asignado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Entrenador no activo o datos de atleta inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    public ResponseEntity<AtletaResponse> asignarAtleta(
            @PathVariable Long entrenadorId,
            @Valid @RequestBody AtletaRequest request
    ) {
        Atleta nuevoAtleta = atletaService.asignarAEntrenador(entrenadorId, request);
        AtletaResponse response = assembler.toModel(nuevoAtleta);

        URI location = linkTo(methodOn(AtletaController.class).obtenerAtletaPorId(nuevoAtleta.getId())).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/api/atletas/{id}")
    @Operation(summary = "Consultar un atleta específico", description = "Obtiene los detalles del atleta con enlaces a su entrenador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atleta encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Atleta no encontrado")
    })
    public ResponseEntity<AtletaResponse> obtenerAtletaPorId(@PathVariable Long id) {
        Atleta atleta = atletaService.obtenerPorId(id);
        return ResponseEntity.ok(assembler.toModel(atleta));
    }

    @DeleteMapping("/api/atletas/{id}")
    @Operation(summary = "Eliminar un atleta", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Atleta eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Atleta no encontrado")
    })
    public ResponseEntity<Void> eliminarAtleta(@PathVariable Long id) {
        atletaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

