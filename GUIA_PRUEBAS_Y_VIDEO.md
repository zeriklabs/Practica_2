# Guía de Pruebas, Evidencias y Guion para el Video Demostrativo

Este documento contiene las evidencias de las **12 pruebas obligatorias** de la rúbrica y un guion estructurado para la grabación del video demostrativo (de 5 a 10 minutos).

---

## 📸 Matriz de Evidencias de las 12 Pruebas Obligatorias

| # | Prueba Obligatoria | Endpoint y Método | Rol / Headers | Código Esperado | Evidencia del Resultado |
|---|---|---|---|---|---|
| **1** | Iniciar sesión con credenciales correctas | `POST /api/auth/login` | Público (`admin` / `admin123`) | `200 OK` | Retorna JWT firmado, `rol: ROLE_ADMIN` y username. |
| **2** | Iniciar sesión con contraseña incorrecta | `POST /api/auth/login` | Público (`admin` / `invalida`) | `401 Unauthorized` | Retorna JSON con mensaje `"Credenciales incorrectas: usuario o contraseña erróneos."`. |
| **3** | Consultar un recurso sin token | `GET /api/entrenadores` | Sin cabecera `Authorization` | `401 Unauthorized` | Retorna `"Acceso no autorizado: Debe proporcionar un token JWT válido..."`. |
| **4** | Consultar un recurso con token válido | `GET /api/entrenadores` | `Bearer <token_consulta>` | `200 OK` | Retorna la colección `_embedded.entrenadores` y enlaces `_links`. |
| **5** | Enviar un token manipulado o expirado | `GET /api/entrenadores` | `Bearer tokenFalso123` | `401 Unauthorized` | Retorna `"Token JWT inválido, expirado o manipulado"`. |
| **6** | Registrar un recurso con rol ADMIN | `POST /api/entrenadores` | `Bearer <token_admin>` | `201 Created` | Retorna cabecera `Location` y cuerpo con enlaces HATEOAS del nuevo recurso. |
| **7** | Registrar un recurso con rol CONSULTA | `POST /api/entrenadores` | `Bearer <token_consulta>` | `403 Forbidden` | Retorna `"Acceso denegado: No posee los permisos requeridos (rol)..."`. |
| **8** | Consultar un recurso individual | `GET /api/entrenadores/1` | `Bearer <token_consulta>` | `200 OK` | Retorna el recurso con enlaces `self`, `entrenadores`, `actualizar`, `eliminar`, `atletas`. |
| **9** | Navegar de una colección a un recurso individual | `GET /api/entrenadores/1/atletas` | `Bearer <token_consulta>` | `200 OK` | Navegación exitosa usando la URL descubierta en el link `atletas`. |
| **10** | Enlaces dinámicos según estado | `GET /api/entrenadores/2` | `Bearer <token_admin>` | `200 OK` | El recurso en estado `INACTIVO` muestra el enlace dinámico `activar` (no muestra `asignar-atleta`). |
| **11** | Consultar un identificador inexistente | `GET /api/entrenadores/999999` | `Bearer <token_consulta>` | `404 Not Found` | Retorna `"No se encontró el entrenador con ID: 999999"`. |
| **12** | Enviar datos inválidos al registrar | `POST /api/entrenadores` | `Bearer <token_admin>` | `400 Bad Request` | Retorna objeto `details` con errores específicos por cada campo inválido. |

---

## 🎬 Guion Estructurado para el Video Demostrativo (5 a 10 Minutos)

### Minuto 0:00 - 1:00 | Introducción y Planteamiento del Problema
- Saludo, presentación del estudiante y título de la práctica: *"Desarrollo de una API REST de Nivel 3 de Richardson Protegida con JWT"*.
- Explicación del dominio elegido: **Gestión de Entrenadores Deportivos y Atletas**.
- Mencionar las tecnologías utilizadas: Java 17+, Spring Boot 3, Spring Data JPA, Spring HATEOAS, Spring Security 6, JJWT y OpenAPI/Swagger.

### Minuto 1:00 - 2:30 | Explicación del Modelo de Madurez de Richardson (Nivel 0 al 3)
- **Nivel 0 (POX):** Uso del protocolo HTTP como transporte fundamental.
- **Nivel 1 (Recursos):** Identificación clara de recursos con URIs semánticas (`/api/entrenadores`, `/api/entrenadores/1`, `/api/atletas`).
- **Nivel 2 (Verbos y Códigos HTTP):** Uso estricto de verbos HTTP (`GET` para consulta, `POST` para creación retornando `201 Created` y cabecera `Location`, `PUT` para actualización, `DELETE` retornando `204 No Content`) y códigos de estado estandarizados.
- **Nivel 3 (HATEOAS - Hypermedia As The Engine Of Application State):** Explicar que el cliente no necesita conocer URLs de antemano; las respuestas incluyen el objeto `_links` con las acciones que el recurso permite ejecutar en ese momento según su estado (ej: si está `ACTIVO` permite `desactivar` o `asignar-atleta`, y si está `INACTIVO` solo permite `activar`).

### Minuto 2:30 - 4:00 | Arquitectura de Seguridad (Spring Security + JWT + Roles)
- Mostrar el código de `SecurityConfig`: sesión `STATELESS`, filtro `JwtAuthenticationFilter`, y reglas de autorización:
  - `ROLE_ADMIN`: Acceso total (CRUD).
  - `ROLE_CONSULTA`: Solo lectura (`GET`).
- Explicar la firma del token con algoritmo HMAC y almacenamiento seguro de contraseñas con BCrypt.

### Minuto 4:00 - 7:30 | Demostración en Vivo en Postman (Pruebas 1 a 12)
1. Ejecutar **Prueba 1**: Login con usuario `admin` -> Copiar token generado.
2. Ejecutar **Prueba 2**: Login con contraseña incorrecta -> Mostrar el error `401 Unauthorized`.
3. Ejecutar **Prueba 3**: Hacer `GET /api/entrenadores` sin token -> Mostrar `401 Unauthorized`.
4. Ejecutar **Prueba 4**: Hacer `GET /api/entrenadores` con token de consulta -> Mostrar la lista con los hiperenlaces `_links`.
5. Ejecutar **Prueba 5**: Enviar un token manipulado -> Mostrar el `401`.
6. Ejecutar **Prueba 6**: Crear un entrenador con rol `ADMIN` -> Mostrar el código `201 Created` y la cabecera `Location`.
7. Ejecutar **Prueba 7**: Intentar crear un entrenador con rol `CONSULTA` -> Mostrar el código `403 Forbidden`.
8. Ejecutar **Prueba 8**: Consultar un entrenador individual -> Mostrar los enlaces `self`, `entrenadores`, `actualizar`, `eliminar`, `atletas`.
9. Ejecutar **Prueba 9**: Navegar usando el enlace de atletas (`/api/entrenadores/1/atletas`) -> Mostrar los atletas asociados con sus propios hiperenlaces.
10. Ejecutar **Prueba 10**: Consultar entrenador inactivo -> Demostrar cómo los enlaces cambian dinámicamente según el estado del recurso (muestra `activar`).
11. Ejecutar **Prueba 11**: Consultar ID inexistente (`/api/entrenadores/999999`) -> Mostrar `404 Not Found`.
12. Ejecutar **Prueba 12**: Enviar datos vacíos/negativos al registrar -> Mostrar `400 Bad Request` con los detalles campo por campo.

### Minuto 7:30 - 8:30 | Documentación Interactiva en Swagger UI
- Abrir el navegador en `http://localhost:8080/swagger-ui.html`.
- Mostrar los tags organizados: `Autenticación`, `Entrenadores`, `Atletas`.
- Presionar el botón verde **Authorize**, ingresar el token JWT y demostrar una consulta interactiva directa desde la interfaz Swagger.

### Minuto 8:30 - 9:00 | Conclusiones
- Resumir cómo se cumplieron todos los requerimientos técnicos y criterios de aceptación.
- Despedida formal.

