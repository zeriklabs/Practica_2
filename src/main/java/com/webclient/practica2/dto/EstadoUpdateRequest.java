package com.webclient.practica2.dto;

import com.webclient.practica2.model.EstadoEntrenador;
import jakarta.validation.constraints.NotNull;

public class EstadoUpdateRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoEntrenador estado;

    public EstadoUpdateRequest() {
    }

    public EstadoUpdateRequest(EstadoEntrenador estado) {
        this.estado = estado;
    }

    public EstadoEntrenador getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntrenador estado) {
        this.estado = estado;
    }
}

