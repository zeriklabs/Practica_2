package com.webclient.practica2.dto;

import com.webclient.practica2.model.EstadoEntrenador;
import jakarta.validation.constraints.*;

public class EntrenadorRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 60, message = "El apellido debe tener entre 2 y 60 caracteres")
    private String apellido;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @NotNull(message = "Los años de experiencia son obligatorios")
    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    private Integer experienciaAnios;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;

    @Pattern(regexp = "^[0-9+()\\-\\s]{7,20}$", message = "El teléfono debe tener un formato válido")
    private String telefono;

    private EstadoEntrenador estado = EstadoEntrenador.ACTIVO;

    public EntrenadorRequest() {
    }

    public EntrenadorRequest(String nombre, String apellido, String especialidad, Integer experienciaAnios, String email, String telefono, EstadoEntrenador estado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.experienciaAnios = experienciaAnios;
        this.email = email;
        this.telefono = telefono;
        this.estado = (estado != null) ? estado : EstadoEntrenador.ACTIVO;
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
}

