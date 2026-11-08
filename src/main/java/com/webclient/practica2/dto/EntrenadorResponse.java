package com.webclient.practica2.dto;

import com.webclient.practica2.model.EstadoEntrenador;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "entrenadores", itemRelation = "entrenador")
public class EntrenadorResponse extends RepresentationModel<EntrenadorResponse> {

    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private Integer experienciaAnios;
    private String email;
    private String telefono;
    private EstadoEntrenador estado;
    private Integer cantidadAtletas;

    public EntrenadorResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Integer getExperienciaAnios() {
        return experienciaAnios;
    }

    public void setExperienciaAnios(Integer experienciaAnios) {
        this.experienciaAnios = experienciaAnios;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public EstadoEntrenador getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntrenador estado) {
        this.estado = estado;
    }

    public Integer getCantidadAtletas() {
        return cantidadAtletas;
    }

    public void setCantidadAtletas(Integer cantidadAtletas) {
        this.cantidadAtletas = cantidadAtletas;
    }
}

