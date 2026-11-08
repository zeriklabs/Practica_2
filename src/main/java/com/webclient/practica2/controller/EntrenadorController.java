package com.webclient.practica2.controller;

import com.webclient.practica2.assembler.EntrenadorModelAssembler;
import com.webclient.practica2.dto.EntrenadorRequest;
import com.webclient.practica2.dto.EntrenadorResponse;
import com.webclient.practica2.dto.EstadoUpdateRequest;
import com.webclient.practica2.model.Entrenador;
import com.webclient.practica2.model.EstadoEntrenador;
import com.webclient.practica2.service.EntrenadorService;
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
@RequestMapping("/api/entrenadores")
@Tag(name = "Entrenadores", description = "Endpoints con soporte HATEOAS (Nivel 3 de Richardson) para gestión de entrenadores")
@SecurityRequirement(name = "BearerAuth")
public class EntrenadorController {

    private final EntrenadorService entrenadorService;
    private final EntrenadorModelAssembler assembler;

    public EntrenadorController(EntrenadorService entrenadorService, EntrenadorModelAssembler assembler) {
        this.entrenadorService = entrenadorService;
        this.assembler = assembler;
    }

    @GetMapping
    @Operation(summary = "Listar todos los entrenadores", description = "Accesible por usuarios con rol ADMIN o CONSULTA. Incluye enlaces HATEOAS para cada recurso.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de entrenadores obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token ausente o inválido")
    })
    public ResponseEntity<CollectionModel<EntrenadorResponse>> obtenerTodos() {
        List<Entrenador> entrenadores = entrenadorService.listarTodos();
        CollectionModel<EntrenadorResponse> collectionModel = assembler.toCollectionModel(entrenadores);

        collectionModel.add(linkTo(methodOn(EntrenadorController.class).obtenerTodos()).withSelfRel());
        collectionModel.add(linkTo(methodOn(EntrenadorController.class).buscar(null, null, null)).withRel("buscar"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un entrenador específico por su ID", description = "Accesible por ADMIN y CONSULTA. Incluye enlaces HATEOAS dinámicos según su estado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenador encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    public ResponseEntity<EntrenadorResponse> obtenerPorId(@PathVariable Long id) {
        Entrenador entrenador = entrenadorService.obtenerPorId(id);
        return ResponseEntity.ok(assembler.toModel(entrenador));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar entrenadores por criterios", description = "Búsqueda por nombre/apellido, especialidad y/o estado. Accesible por ADMIN y CONSULTA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<CollectionModel<EntrenadorResponse>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) EstadoEntrenador estado
    ) {
        List<Entrenador> resultados = entrenadorService.buscar(nombre, especialidad, estado);
        CollectionModel<EntrenadorResponse> collectionModel = assembler.toCollectionModel(resultados);

        collectionModel.add(linkTo(methodOn(EntrenadorController.class).buscar(nombre, especialidad, estado)).withSelfRel());
        collectionModel.add(linkTo(methodOn(EntrenadorController.class).obtenerTodos()).withRel("todos-los-entrenadores"));

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo entrenador", description = "Requiere rol ADMIN. Retorna el recurso creado con enlaces HATEOAS y código 201 Created.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entrenador creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
    })
    public ResponseEntity<EntrenadorResponse> crear(@Valid @RequestBody EntrenadorRequest request) {
        Entrenador nuevo = entrenadorService.crear(request);
        EntrenadorResponse response = assembler.toModel(nuevo);

        URI location = linkTo(methodOn(EntrenadorController.class).obtenerPorId(nuevo.getId())).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de un entrenador", description = "Requiere rol ADMIN. Actualiza datos y retorna representación actualizada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenador actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    public ResponseEntity<EntrenadorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EntrenadorRequest request
    ) {
        Entrenador actualizado = entrenadorService.actualizar(id, request);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del entrenador (ACTIVO, INACTIVO, SUSPENDIDO)", description = "Requiere rol ADMIN. Demuestra la transición de estado y enlaces dinámicos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado modificado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    public ResponseEntity<EntrenadorResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoUpdateRequest request
    ) {
        Entrenador modificado = entrenadorService.cambiarEstado(id, request.getEstado());
        return ResponseEntity.ok(assembler.toModel(modificado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un entrenador", description = "Requiere rol ADMIN. Retorna código 204 No Content.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entrenador eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entrenadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

