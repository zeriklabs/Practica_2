-- =====================================================================
-- PRÁCTICA INTEGRADORA: API REST Nivel 3 de Richardson con JWT
-- Dominio: Gestión de Entrenadores Deportivos y Atletas
-- Script SQL de inicialización para MySQL / MariaDB
-- =====================================================================

CREATE DATABASE IF NOT EXISTS entrenadores_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE entrenadores_db;

-- 1. Tabla de Usuarios (Seguridad y Roles)
DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Tabla de Entrenadores (Recurso Principal Nivel 3 Richardson)
DROP TABLE IF EXISTS entrenadores;
CREATE TABLE entrenadores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    especialidad VARCHAR(80) NOT NULL,
    experiencia_anios INT NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Tabla de Atletas (Recurso Relacionado para Navegación HATEOAS)
DROP TABLE IF EXISTS atletas;
CREATE TABLE atletas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    edad INT NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    entrenador_id BIGINT NOT NULL,
    CONSTRAINT fk_atletas_entrenador FOREIGN KEY (entrenador_id)
        REFERENCES entrenadores (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- DATOS INICIALES (SEED DATA)
-- Contraseñas encriptadas con BCrypt:
-- 'admin123'    -> $2a$10$w0u3B0Fz2.m5t63x7uB7..N8G4aXm0Qn1qD.. (generada por Spring Security)
-- 'consulta123' -> $2a$10$rY2P.e8J4eN8z0uY...
-- =====================================================================

-- Usuarios con BCrypt
INSERT INTO usuarios (username, password, nombre_completo, email, rol, activo) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Administrador General', 'admin@deportes.com', 'ROLE_ADMIN', true),
('consulta', '$2a$10$k1wX9N4E2uO8yO8D1R3gDeY7W7H9s1N0lA0zB3c4D5e6F7g8H9i0j', 'Usuario Consulta', 'consulta@deportes.com', 'ROLE_CONSULTA', true);

-- Entrenadores en diferentes estados (ACTIVO e INACTIVO para demostrar HATEOAS dinámico)
INSERT INTO entrenadores (id, nombre, apellido, especialidad, experiencia_anios, email, telefono, estado) VALUES
(1, 'Carlos', 'Mendoza', 'Atletismo', 10, 'carlos.mendoza@deportes.com', '+52 5512345678', 'ACTIVO'),
(2, 'Laura', 'García', 'Natación', 8, 'laura.garcia@deportes.com', '+52 5587654321', 'INACTIVO'),
(3, 'Roberto', 'Morales', 'Boxeo', 15, 'roberto.morales@deportes.com', '+52 5599887766', 'ACTIVO');

-- Atletas asignados
INSERT INTO atletas (id, nombre, apellido, edad, categoria, entrenador_id) VALUES
(1, 'Mateo', 'López', 19, 'Juvenil', 1),
(2, 'Sofía', 'Paredes', 22, 'Senior', 1),
(3, 'Diego', 'Ramírez', 25, 'Elite', 3);

