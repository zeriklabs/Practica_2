# Práctica Integradora: API REST Nivel 3 de Richardson Protegida con JWT

> **Materia:** Desarrollo de Aplicaciones Web Client  
> **Modalidad:** Individual  
> **Dominio Implementado:** Gestión de Entrenadores Deportivos y Atletas  
> **Tecnologías:** Java 17+, Spring Boot 3.3.5, Spring HATEOAS, Spring Security 6, JJWT (io.jsonwebtoken 0.12.6), Spring Data JPA, H2 / MySQL, SpringDoc OpenAPI (Swagger UI).

---

## 📑 Tabla de Contenidos
1. [Descripción General](#descripción-general)
2. [Cumplimiento del Modelo de Madurez de Richardson](#cumplimiento-del-modelo-de-madurez-de-richardson)
3. [Seguridad y Control de Acceso (JWT & Roles)](#seguridad-y-control-de-acceso-jwt--roles)
4. [Estructura del Proyecto (Capas)](#estructura-del-proyecto-capas)
5. [Instrucciones de Ejecución](#instrucciones-de-ejecución)
6. [Credenciales por Defecto](#credenciales-por-defecto)
7. [Documentación Swagger / OpenAPI](#documentación-swagger--openapi)
8. [Colección de Postman y las 12 Pruebas Obligatorias](#colección-de-postman-y-las-12-pruebas-obligatorias)
9. [Despliegue en Render](#despliegue-en-render)
10. [Archivos Entregables del Proyecto](#archivos-entregables-del-proyecto)

---

## 1. Descripción General
Esta API RESTful fue desarrollada para cumplir rigurosamente con los cuatro niveles de madurez de Leonard Richardson, integrando hiperenlaces dinámicos mediante **Spring HATEOAS** que permiten la navegabilidad y descubrimiento del estado de los recursos sin necesidad de codificar URLs fijas en el cliente. Adicionalmente, cuenta con autenticación basada en tokens **JSON Web Token (JWT)** y autorización por roles segregada (`ADMIN` y `CONSULTA`).

---

## 2. Cumplimiento del Modelo de Madurez de Richardson

- **Nivel 0 (POX):** El protocolo HTTP se utiliza como medio de transferencia de información.
- **Nivel 1 (Recursos):** Cada entidad se identifica mediante URIs semánticas únicas:
  - `/api/entrenadores` (Colección de entrenadores)
  - `/api/entrenadores/{id}` (Entrenador específico)
  - `/api/entrenadores/{id}/atletas` (Colección de atletas de un entrenador)
  - `/api/atletas/{id}` (Atleta individual)
- **Nivel 2 (Verbos y Códigos de Estado HTTP):**
  - `GET`: Consultas (`200 OK`).
  - `POST`: Creación (`201 Created` con cabecera `Location`).
  - `PUT`: Actualización (`200 OK`).
  - `PATCH`: Modificación parcial de estado (`200 OK`).
  - `DELETE`: Eliminación (`204 No Content`).
  - Códigos de error específicos: `400 Bad Request` con validaciones, `401 Unauthorized`, `403 Forbidden` y `404 Not Found`.
- **Nivel 3 (HATEOAS - Hypermedia As The Engine Of Application State):**
  - Todas las respuestas JSON incluyen el objeto `_links`.
  - Enlaces dinámicos:
    - `self`: Enlace canónico al propio recurso.
    - `entrenadores`: Enlace a la colección principal.
    - `actualizar`: URI para actualizar.
    - `eliminar`: URI para eliminar.
    - `atletas`: URI para consultar los atletas asignados.
    - **Enlaces dependientes del estado:**
      - Si el entrenador está `ACTIVO`, ofrece enlaces `desactivar` y `asignar-atleta`.
      - Si el entrenador está `INACTIVO`, ofrece el enlace `activar` y oculta la posibilidad de asignarle atletas.

---

## 3. Seguridad y Control de Acceso (JWT & Roles)

- **Sesión Stateless:** `SessionCreationPolicy.STATELESS` en Spring Security.
- **Cifrado de Contraseñas:** Algoritmo `BCryptPasswordEncoder`.
- **Filtro JWT:** `JwtAuthenticationFilter` (`OncePerRequestFilter`) que intercepta la cabecera `Authorization: Bearer <token>`.
- **Roles:**
  - `ROLE_ADMIN`: Consultar, registrar, actualizar, cambiar estado y eliminar recursos (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`).
  - `ROLE_CONSULTA`: Solo lectura (`GET`). Cualquier intento de escritura (`POST`, `PUT`, `DELETE`) retorna `403 Forbidden`.
- **Endpoints Públicos:**
  - `/api/auth/login` (Inicio de sesión y entrega de JWT).
  - `/api/auth/registro` (Creación pública de cuenta).
  - `/swagger-ui/**`, `/v3/api-docs/**` (Documentación OpenAPI).
  - `/h2-console/**` (Consola de base de datos para desarrollo).

---

## 4. Estructura del Proyecto (Capas)

```
src/main/java/com/webclient/practica2/
├── Practica2Application.java
├── assembler/                 # Ensambladores de Spring HATEOAS (Nivel 3)
│   ├── EntrenadorModelAssembler.java
│   └── AtletaModelAssembler.java
├── config/                    # Configuraciones y datos semilla
│   ├── DataInitializer.java
│   └── OpenApiConfig.java
├── controller/                # Controladores REST
│   ├── AuthController.java
│   ├── EntrenadorController.java
│   └── AtletaController.java
├── dto/                       # Objetos de Transferencia de Datos
│   ├── AuthRequest.java, AuthResponse.java, RegistroRequest.java
│   ├── EntrenadorRequest.java, EntrenadorResponse.java
│   ├── AtletaRequest.java, AtletaResponse.java
│   ├── EstadoUpdateRequest.java
│   └── ErrorResponse.java
├── exception/                 # Manejo centralizado de excepciones
│   ├── BadRequestException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── model/                     # Entidades JPA
│   ├── Entrenador.java, Atleta.java, Usuario.java
│   ├── EstadoEntrenador.java (Enum: ACTIVO, INACTIVO, SUSPENDIDO)
│   └── Rol.java (Enum: ROLE_ADMIN, ROLE_CONSULTA)
├── repository/                # Interfaces Spring Data JPA
│   ├── EntrenadorRepository.java
│   ├── AtletaRepository.java
│   └── UsuarioRepository.java
├── security/                  # Configuración de Seguridad Spring Security 6
│   ├── SecurityConfig.java
│   ├── CustomUserDetailsService.java
│   └── jwt/
│       ├── JwtService.java
│       ├── JwtAuthenticationFilter.java
│       ├── JwtAuthenticationEntryPoint.java (Respuesta 401 JSON)
│       └── JwtAccessDeniedHandler.java (Respuesta 403 JSON)
└── service/                   # Capa de Lógica de Negocio
    ├── AuthService.java
    ├── EntrenadorService.java
    └── AtletaService.java
```

---

## 5. Instrucciones de Ejecución

### Opción A: Ejecución Directa (Recomendada)
El proyecto viene configurado por defecto con base de datos **H2 en memoria** y carga automática de datos iniciales (`DataInitializer`), por lo que inicia inmediatamente sin requerir configuraciones de software adicionales:

```bash
# En Windows:
.\mvnw.cmd spring-boot:run

# En Linux/macOS:
./mvnw spring-boot:run
```

La aplicación iniciará en: `http://localhost:8080`

### Opción B: Ejecución con MySQL / MariaDB
1. Crear la base de datos ejecutando el script `database.sql`.
2. En `src/main/resources/application.properties`, descomentar la sección de MySQL y configurar las credenciales de tu servidor.
3. Iniciar la aplicación con `.\mvnw.cmd spring-boot:run`.

---

## 6. Credenciales por Defecto

Al arrancar la aplicación, se crean automáticamente los siguientes usuarios de prueba:

| Usuario | Contraseña | Rol | Permisos |
|---|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN` | CRUD completo (Crear, Consultar, Actualizar, Borrar) |
| `consulta` | `consulta123` | `ROLE_CONSULTA` | Solo lectura (Consultas y Búsquedas `GET`) |

---

## 7. Documentación Swagger / OpenAPI

La documentación interactiva se encuentra disponible en:
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Uso con JWT en Swagger UI:**
> 1. Iniciar sesión en el endpoint `POST /api/auth/login` con el usuario `admin`.
> 2. Copiar el valor del `token`.
> 3. En la parte superior derecha de Swagger, hacer clic en el botón verde **Authorize**.
> 4. Pegar el token en el campo y hacer clic en **Authorize**.

---

## 8. Colección de Postman y las 12 Pruebas Obligatorias

El archivo `Practica2_Postman_Collection.json` está listo para ser importado en Postman.
Contiene scripts automáticos de prueba que extraen y reutilizan los tokens JWT en las variables `token_admin` y `token_consulta`.

### Resumen de las 12 Pruebas Obligatorias

| # | Prueba Obligatoria | Endpoint | Resultado Esperado |
|---|---|---|---|
| **1** | Iniciar sesión con credenciales correctas | `POST /api/auth/login` | `200 OK` (Entrega JWT válido) |
| **2** | Iniciar sesión con contraseña incorrecta | `POST /api/auth/login` | `401 Unauthorized` (Rechaza autenticación) |
| **3** | Consultar un recurso sin token | `GET /api/entrenadores` | `401 Unauthorized` (Acceso denegado) |
| **4** | Consultar un recurso con token válido | `GET /api/entrenadores` | `200 OK` (Retorna colección con enlaces HATEOAS) |
| **5** | Enviar un token manipulado o expirado | `GET /api/entrenadores` | `401 Unauthorized` (Rechaza solicitud) |
| **6** | Registrar un recurso con rol ADMIN | `POST /api/entrenadores` | `201 Created` (Registra y devuelve cabecera `Location`) |
| **7** | Registrar un recurso con rol CONSULTA | `POST /api/entrenadores` | `403 Forbidden` (Deniega operación por falta de permisos) |
| **8** | Consultar un recurso individual | `GET /api/entrenadores/1` | `200 OK` (Incluye enlaces `self`, `actualizar`, `eliminar`, etc.) |
| **9** | Navegar de colección a recurso individual | `GET /api/entrenadores/1/atletas` | `200 OK` (Navega exitosamente usando hiperenlaces) |
| **10** | Enlaces dinámicos según estado | `GET /api/entrenadores/2` | `200 OK` (Entrenador `INACTIVO` muestra enlace `activar`) |
| **11** | Consultar un identificador inexistente | `GET /api/entrenadores/999999` | `404 Not Found` (Mensaje explicativo) |
| **12** | Enviar datos inválidos al registrar | `POST /api/entrenadores` | `400 Bad Request` (Retorna detalles de validación por campo) |

---

## 9. Despliegue en Render

El repositorio incluye los archivos para despliegue automático en la nube:
- `Dockerfile`: Construcción optimizada multi-stage con OpenJDK 17.
- `render.yaml`: Manifiesto de servicio para Render Web Service.

### Pasos para desplegar:
1. Subir este proyecto a un repositorio de GitHub / GitLab.
2. En [Render.com](https://render.com/), crear un **New Web Service** y conectar el repositorio.
3. Seleccionar el entorno **Docker**.
4. En **Health Check Path**, configurar `/v3/api-docs`.
5. Una vez desplegado, la URL de Swagger será: `https://<tu-servicio-render>.onrender.com/swagger-ui.html`.

---

## 10. Archivos Entregables del Proyecto

1. **Código fuente:** Proyecto Spring Boot completo organizado en capas (`src/main/java`).
2. **Base de datos:** [database.sql](database.sql) con script DDL y DML para MySQL / MariaDB.
3. **Documentación de endpoints:** [DOCUMENTACION_ENDPOINTS.md](DOCUMENTACION_ENDPOINTS.md).
4. **Diagrama de arquitectura:** [ARQUITECTURA.md](ARQUITECTURA.md) (Diagrama Mermaid y especificación de capas).
5. **Colección de Postman:** [Practica2_Postman_Collection.json](Practica2_Postman_Collection.json).
6. **Evidencias y Guion de Video:** [GUIA_PRUEBAS_Y_VIDEO.md](GUIA_PRUEBAS_Y_VIDEO.md) (Estructura de 5 a 10 minutos).
7. **Configuración de Despliegue:** [Dockerfile](Dockerfile) y [render.yaml](render.yaml).

