package com.webclient.practica2.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "entrenadores")
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String apellido;

    @Column(nullable = false, length = 80)
    private String especialidad;

    @Column(name = "experiencia_anios", nullable = false)
    private Integer experienciaAnios;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEntrenador estado = EstadoEntrenador.ACTIVO;

    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Atleta> atletas = new ArrayList<>();

    public Entrenador() {
    }

    public Entrenador(String nombre, String apellido, String especialidad, Integer experienciaAnios, String email, String telefono, EstadoEntrenador estado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.experienciaAnios = experienciaAnios;
        this.email = email;
        this.telefono = telefono;
        this.estado = (estado != null) ? estado : EstadoEntrenador.ACTIVO;
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

    public List<Atleta> getAtletas() {
        return atletas;
    }

    public void setAtletas(List<Atleta> atletas) {
        this.atletas = atletas;
    }

    public void agregarAtleta(Atleta atleta) {
        this.atletas.add(atleta);
        atleta.setEntrenador(this);
    }

    public void removerAtleta(Atleta atleta) {
        this.atletas.remove(atleta);
        atleta.setEntrenador(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrenador that = (Entrenador) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

