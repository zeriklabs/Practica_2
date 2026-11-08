package com.webclient.practica2.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AtletaRequest {

    @NotBlank(message = "El nombre del atleta es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido del atleta es obligatorio")
    private String apellido;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 5, message = "La edad mínima es de 5 años")
    @Max(value = 100, message = "La edad máxima es de 100 años")
    private Integer edad;

    @NotBlank(message = "La categoría es obligatoria (ej: Juvenil, Senior, Master)")
    private String categoria;

    public AtletaRequest() {
    }

    public AtletaRequest(String nombre, String apellido, Integer edad, String categoria) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.categoria = categoria;
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

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}

