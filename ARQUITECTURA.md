# Diagrama de Arquitectura de la Solución

La aplicación está diseñada bajo el patrón de **Arquitectura en Capas Limpia (Layered Architecture)**, separando de manera estricta las responsabilidades de presentación (REST / HATEOAS), lógica de negocio (Servicios), acceso a datos (Spring Data JPA) y la capa transversal de seguridad (Spring Security & JWT).

---

## 1. Diagrama de Componentes y Flujo de Peticiones

```mermaid
flowchart TD
    subgraph Cliente["Cliente HTTP"]
        Postman["Postman / Web Client / Swagger UI"]
    end

    subgraph CapaSeguridad["Capa de Seguridad (Spring Security 6 & JWT)"]
        FilterChain["SecurityFilterChain (Stateless)"]
        JwtFilter["JwtAuthenticationFilter (OncePerRequestFilter)"]
        JwtService["JwtService (Firma HMAC-SHA256 & Validación)"]
        UserDetailsService["CustomUserDetailsService"]
        AuthEntryPoint["JwtAuthenticationEntryPoint (401 JSON)"]
        AccessDenied["JwtAccessDeniedHandler (403 JSON)"]
    end

    subgraph CapaControladores["Capa de Controladores (Spring Web)"]
        AuthController["AuthController (/api/auth)"]
        EntrenadorController["EntrenadorController (/api/entrenadores)"]
        AtletaController["AtletaController (/api/atletas)"]
        GlobalException["GlobalExceptionHandler (@RestControllerAdvice)"]
    end

    subgraph CapaAssemblers["Capa HATEOAS (Nivel 3 Richardson)"]
        EntrenadorAssembler["EntrenadorModelAssembler (Enlaces Dinámicos _links)"]
        AtletaAssembler["AtletaModelAssembler (Navegación Hipermedia)"]
    end

    subgraph CapaServicios["Capa de Servicios de Negocio"]
        AuthService["AuthService (Login & Registro)"]
        EntrenadorService["EntrenadorService (Reglas de Estado & Búsqueda)"]
        AtletaService["AtletaService (Asignación & Validación)"]
    end

    subgraph CapaRepositorios["Capa de Persistencia (Spring Data JPA)"]
        UsuarioRepo["UsuarioRepository"]
        EntrenadorRepo["EntrenadorRepository"]
        AtletaRepo["AtletaRepository"]
    end

    subgraph BaseDatos["Persistencia"]
        DB[("MySQL / MariaDB / H2")]
    end

    %% Flujos de interacción
    Postman -->|1. Petición HTTP + Bearer Token| FilterChain
    FilterChain --> JwtFilter
    JwtFilter -->|Valida Token| JwtService
    JwtFilter -->|Carga Permisos/Roles| UserDetailsService
    FilterChain -->|No Autenticado| AuthEntryPoint
    FilterChain -->|Rol Insuficiente| AccessDenied

    FilterChain -->|Petición Autorizada| CapaControladores
    AuthController --> AuthService
    EntrenadorController --> EntrenadorService
    AtletaController --> AtletaService

    EntrenadorController --> EntrenadorAssembler
    AtletaController --> AtletaAssembler

    AuthService --> UsuarioRepo
    EntrenadorService --> EntrenadorRepo
    AtletaService --> AtletaRepo
    AtletaService --> EntrenadorRepo

    UsuarioRepo --> DB
    EntrenadorRepo --> DB
    AtletaRepo --> DB
```

---

## 2. Descripción de Capas

### 2.1 Capa de Seguridad Transversal
- **`SecurityConfig`:** Configura una política de sesión sin estado (`SessionCreationPolicy.STATELESS`), deshabilita CSRF (adecuado para APIs REST basados en tokens) y restringe endpoints por rol.
- **`JwtAuthenticationFilter`:** Intercepta cada solicitud entrante, extrae el token del encabezado `Authorization: Bearer <token>`, valida la firma criptográfica y establece el contexto de seguridad (`SecurityContextHolder`).
- **`JwtAuthenticationEntryPoint` y `JwtAccessDeniedHandler`:** Garantizan que cualquier falla de autenticación devuelva un código HTTP estándar `401 Unauthorized` en formato JSON estructurado, y que un usuario con privilegios insuficientes reciba un código `403 Forbidden`.

### 2.2 Capa de Presentación y Controladores
- **`AuthController`:** Expone los endpoints públicos `/api/auth/login` y `/api/auth/registro`.
- **`EntrenadorController`:** Implementa los métodos CRUD y búsquedas, devolviendo modelos `CollectionModel` y `RepresentationModel` con hiperenlaces.
- **`AtletaController`:** Permite descubrir y gestionar los atletas asociados a un entrenador mediante enlaces hipermedia.
- **`GlobalExceptionHandler`:** Captura excepciones de validación (`MethodArgumentNotValidException`), errores 404 (`ResourceNotFoundException`) y errores 400 (`BadRequestException`), unificando las respuestas de error en un DTO estándar `ErrorResponse`.

### 2.3 Capa de Ensambladores HATEOAS (Nivel 3 de Richardson)
- **`EntrenadorModelAssembler`:** Transforma entidades JPA `Entrenador` a DTOs `EntrenadorResponse` enriquecidos con enlaces HATEOAS (`_links`). Los enlaces son dinámicos:
  - Si el entrenador está `ACTIVO`, ofrece enlaces para `desactivar` y `asignar-atleta`.
  - Si el entrenador está `INACTIVO`, ofrece el enlace para `activar`.
- **`AtletaModelAssembler`:** Incluye enlaces hacia el recurso `entrenador` padre y navegación inversa.

### 2.4 Capa de Lógica de Negocio (Servicios)
- Contiene las validaciones de negocio (por ejemplo, evitar correos duplicados y verificar que no se asignen atletas a entrenadores inactivos).
- Gestiona las transacciones con `@Transactional`.

### 2.5 Capa de Persistencia (Spring Data JPA)
- Interfaces de repositorio que extienden `JpaRepository` con consultas personalizadas `@Query` para búsquedas dinámicas con filtros combinados.

