# Documentación de Endpoints - API REST Nivel 3 de Richardson

Esta API implementa el **Nivel 3 del Modelo de Madurez de Richardson (HATEOAS)** y está protegida mediante **Spring Security y JSON Web Token (JWT)** con control de acceso basado en roles (`ROLE_ADMIN` y `ROLE_CONSULTA`).

---

## 1. Autenticación (`/api/auth`)

### 1.1 Iniciar Sesión (`POST /api/auth/login`)
- **Acceso:** Público (sin token).
- **Descripción:** Valida credenciales contra la base de datos (con contraseñas hasheadas en BCrypt) y genera un token JWT firmado.
- **Request Body (`application/json`):**
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tipo": "Bearer",
    "username": "admin",
    "rol": "ROLE_ADMIN",
    "mensaje": "Autenticación exitosa. Use el token en la cabecera 'Authorization: Bearer <token>'."
  }
  ```
- **Response `401 Unauthorized` (Credenciales inválidas):**
  ```json
  {
    "timestamp": "2026-11-07T20:14:58",
    "status": 401,
    "error": "Unauthorized",
    "message": "Credenciales incorrectas: usuario o contraseña erróneos.",
    "path": "/api/auth/login"
  }
  ```

---

### 1.2 Registrar Usuario (`POST /api/auth/registro`)
- **Acceso:** Público.
- **Request Body (`application/json`):**
  ```json
  {
    "username": "juan_perez",
    "password": "passwordSeguro123",
    "nombreCompleto": "Juan Pérez",
    "email": "juan.perez@deportes.com",
    "rol": "ROLE_CONSULTA"
  }
  ```
- **Response `201 Created`:** Retorna el usuario registrado junto con su token JWT listo para usar.

---

## 2. Gestión de Entrenadores (`/api/entrenadores`)

> **Cabecera obligatoria para endpoints protegidos:**
> `Authorization: Bearer <token_jwt>`

### 2.1 Listar todos los Entrenadores (`GET /api/entrenadores`)
- **Acceso:** Protegido (`ROLE_ADMIN`, `ROLE_CONSULTA`).
- **Descripción:** Devuelve la colección completa de entrenadores con enlaces HATEOAS a nivel de colección y a nivel de cada elemento.
- **Response `200 OK`:**
  ```json
  {
    "_embedded": {
      "entrenadores": [
        {
          "id": 1,
          "nombre": "Carlos",
          "apellido": "Mendoza",
          "especialidad": "Atletismo",
          "experienciaAnios": 10,
          "email": "carlos.mendoza@deportes.com",
          "telefono": "+52 5512345678",
          "estado": "ACTIVO",
          "cantidadAtletas": 2,
          "_links": {
            "self": { "href": "http://localhost:8080/api/entrenadores/1" },
            "entrenadores": { "href": "http://localhost:8080/api/entrenadores" },
            "actualizar": { "href": "http://localhost:8080/api/entrenadores/1" },
            "eliminar": { "href": "http://localhost:8080/api/entrenadores/1" },
            "atletas": { "href": "http://localhost:8080/api/entrenadores/1/atletas" },
            "desactivar": { "href": "http://localhost:8080/api/entrenadores/1/estado" },
            "asignar-atleta": { "href": "http://localhost:8080/api/entrenadores/1/atletas" }
          }
        }
      ]
    },
    "_links": {
      "self": { "href": "http://localhost:8080/api/entrenadores" },
      "buscar": { "href": "http://localhost:8080/api/entrenadores/buscar{?nombre,especialidad,estado}", "templated": true }
    }
  }
  ```

---

### 2.2 Consultar Entrenador Específico (`GET /api/entrenadores/{id}`)
- **Acceso:** Protegido (`ROLE_ADMIN`, `ROLE_CONSULTA`).
- **Descripción:** Obtiene un entrenador individual. Si el entrenador está `ACTIVO`, ofrece enlaces para `desactivar` y `asignar-atleta`. Si está `INACTIVO`, ofrece el enlace para `activar`.
- **Response `200 OK`:** Enlaces HATEOAS completos adaptados al estado actual del recurso.
- **Response `404 Not Found`:** Si el ID no existe.

---

### 2.3 Buscar Entrenadores por Criterios (`GET /api/entrenadores/buscar`)
- **Acceso:** Protegido (`ROLE_ADMIN`, `ROLE_CONSULTA`).
- **Parámetros de consulta (Query Params):**
  - `nombre` (opcional, String): Coincidencia parcial con nombre o apellido.
  - `especialidad` (opcional, String): Coincidencia con disciplina deportiva.
  - `estado` (opcional, String): `ACTIVO`, `INACTIVO`, `SUSPENDIDO`.
- **Ejemplo:** `GET /api/entrenadores/buscar?nombre=Carlos&estado=ACTIVO`

---

### 2.4 Registrar Entrenador (`POST /api/entrenadores`)
- **Acceso:** Protegido (**SOLO `ROLE_ADMIN`**). Un usuario con `ROLE_CONSULTA` recibe `403 Forbidden`.
- **Request Body (`application/json`):**
  ```json
  {
    "nombre": "Mariana",
    "apellido": "Torres",
    "especialidad": "Gimnasia Olímpica",
    "experienciaAnios": 7,
    "email": "mariana.torres@deportes.com",
    "telefono": "+52 5544332211",
    "estado": "ACTIVO"
  }
  ```
- **Response `201 Created`:**
  - Cabecera: `Location: http://localhost:8080/api/entrenadores/{id}`
  - Cuerpo: Representación completa del entrenador con sus hiperenlaces.
- **Response `400 Bad Request`:** Si hay campos vacíos, email inválido o experiencia negativa.

---

### 2.5 Actualizar Entrenador (`PUT /api/entrenadores/{id}`)
- **Acceso:** Protegido (**SOLO `ROLE_ADMIN`**).
- **Request Body:** Datos completos actualizados.
- **Response `200 OK`:** Entrenador modificado con hiperenlaces actualizados.

---

### 2.6 Cambiar Estado del Entrenador (`PATCH /api/entrenadores/{id}/estado`)
- **Acceso:** Protegido (**SOLO `ROLE_ADMIN`**).
- **Request Body:**
  ```json
  {
    "estado": "INACTIVO"
  }
  ```
- **Response `200 OK`:** Al cambiar a `INACTIVO`, el enlace `asignar-atleta` desaparece y aparece `activar`.

---

### 2.7 Eliminar Entrenador (`DELETE /api/entrenadores/{id}`)
- **Acceso:** Protegido (**SOLO `ROLE_ADMIN`**).
- **Response `204 No Content`** sin cuerpo.

---

## 3. Sub-recurso de Atletas (`/api/atletas` y navegación HATEOAS)

### 3.1 Consultar Atletas de un Entrenador (`GET /api/entrenadores/{entrenadorId}/atletas`)
- **Acceso:** Protegido (`ROLE_ADMIN`, `ROLE_CONSULTA`).
- **Descripción:** Demuestra la navegación hipermedia entre recursos agregados. Cada atleta contiene un enlace a su entrenador y un enlace `self`.

### 3.2 Asignar Atleta a un Entrenador (`POST /api/entrenadores/{entrenadorId}/atletas`)
- **Acceso:** Protegido (**SOLO `ROLE_ADMIN`**).
- **Restricción de Negocio:** Solo se pueden asignar atletas si el entrenador se encuentra en estado `ACTIVO`.

---

## 4. Matriz de Códigos de Estado HTTP Utilizados

| Código HTTP | Nombre | Caso de Uso en la API |
|---|---|---|
| **200** | OK | Búsquedas, consultas individuales y colecciones exitosas. |
| **201** | Created | Registro exitoso de usuarios, entrenadores y atletas (con cabecera `Location`). |
| **204** | No Content | Eliminación exitosa de recursos. |
| **400** | Bad Request | Errores de validación de campos (`@Valid`), datos malformados o reglas de negocio incumplidas. |
| **401** | Unauthorized | Credenciales incorrectas, ausencia de token o token JWT manipulado/expirado. |
| **403** | Forbidden | Usuario autenticado pero sin rol suficiente (ej. `ROLE_CONSULTA` intentando crear o borrar). |
| **404** | Not Found | Entrenador o atleta no encontrado por su identificador. |

