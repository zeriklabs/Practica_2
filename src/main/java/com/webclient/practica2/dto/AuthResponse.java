package com.webclient.practica2.dto;

public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private String username;
    private String rol;
    private String mensaje;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String rol, String mensaje) {
        this.token = token;
        this.tipo = "Bearer";
        this.username = username;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

