package com.webclient.practica2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webclient.practica2.dto.AuthRequest;
import com.webclient.practica2.dto.EntrenadorRequest;
import com.webclient.practica2.model.EstadoEntrenador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Practica2ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtenerToken(String username, String password) throws Exception {
        AuthRequest loginRequest = new AuthRequest(username, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("Prueba 1: Iniciar sesión con credenciales correctas -> Obtiene JWT válido")
    void prueba1_loginCorrecto() throws Exception {
        System.out.println(">>> EJECUTANDO PRUEBA 1 <<<");
        AuthRequest loginRequest = new AuthRequest("admin", "admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.rol", is("ROLE_ADMIN")));
    }

    @Test
    @Order(2)
    @DisplayName("Prueba 2: Iniciar sesión con contraseña incorrecta -> Se rechaza la autenticación (401)")
    void prueba2_loginIncorrecto() throws Exception {
        AuthRequest loginRequest = new AuthRequest("admin", "password_invalida");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("Credenciales incorrectas")));
    }

    @Test
    @Order(3)
    @DisplayName("Prueba 3: Consultar un recurso sin token -> Se rechaza el acceso (401)")
    void prueba3_consultarSinToken() throws Exception {
        mockMvc.perform(get("/api/entrenadores"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("Acceso no autorizado")));
    }

    @Test
    @Order(4)
    @DisplayName("Prueba 4: Consultar un recurso con token válido -> Se obtiene la respuesta correspondiente (200)")
    void prueba4_consultarConTokenValido() throws Exception {
        String token = obtenerToken("consulta", "consulta123");

        mockMvc.perform(get("/api/entrenadores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.entrenadores", notNullValue()))
                .andExpect(jsonPath("$._links.self", notNullValue()));
    }

    @Test
    @Order(5)
    @DisplayName("Prueba 5: Enviar un token manipulado o expirado -> Se rechaza la solicitud (401)")
    void prueba5_tokenManipulado() throws Exception {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidPayload.invalidSignature";

        mockMvc.perform(get("/api/entrenadores")
                        .header("Authorization", "Bearer " + tokenInvalido))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    @Order(6)
    @DisplayName("Prueba 6: Registrar un recurso con rol ADMIN -> Se registra correctamente (201)")
    void prueba6_registrarConAdmin() throws Exception {
        String tokenAdmin = obtenerToken("admin", "admin123");

        EntrenadorRequest request = new EntrenadorRequest(
                "Mariana",
                "Torres",
                "Gimnasia Olímpica",
                7,
                "mariana.torres@deportes.com",
                "+52 5544332211",
                EstadoEntrenador.ACTIVO
        );

        mockMvc.perform(post("/api/entrenadores")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre", is("Mariana")))
                .andExpect(jsonPath("$._links.self", notNullValue()));
    }

    @Test
    @Order(7)
    @DisplayName("Prueba 7: Registrar un recurso con rol CONSULTA -> Se deniega la operación (403)")
    void prueba7_registrarConConsulta() throws Exception {
        String tokenConsulta = obtenerToken("consulta", "consulta123");

        EntrenadorRequest request = new EntrenadorRequest(
                "Juan",
                "Pérez",
                "Tenis",
                5,
                "juan.perez@deportes.com",
                "+52 5599881122",
                EstadoEntrenador.ACTIVO
        );

        mockMvc.perform(post("/api/entrenadores")
                        .header("Authorization", "Bearer " + tokenConsulta)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.message", containsString("Acceso denegado")));
    }

    @Test
    @Order(8)
    @DisplayName("Prueba 8: Consultar un recurso individual -> La respuesta incluye enlaces HATEOAS")
    void prueba8_consultarIndividualHateoas() throws Exception {
        String token = obtenerToken("consulta", "consulta123");

        mockMvc.perform(get("/api/entrenadores/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/entrenadores/1")))
                .andExpect(jsonPath("$._links.entrenadores.href", containsString("/api/entrenadores")))
                .andExpect(jsonPath("$._links.actualizar.href", containsString("/api/entrenadores/1")))
                .andExpect(jsonPath("$._links.eliminar.href", containsString("/api/entrenadores/1")))
                .andExpect(jsonPath("$._links.atletas.href", containsString("/api/entrenadores/1/atletas")));
    }

    @Test
    @Order(9)
    @DisplayName("Prueba 9: Navegar de una colección a un recurso individual -> Enlaces permiten navegación")
    void prueba9_navegacionHateoas() throws Exception {
        String token = obtenerToken("consulta", "consulta123");

        // 1. Obtener la colección y extraer el link self del primer elemento
        MvcResult coleccionResult = mockMvc.perform(get("/api/entrenadores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(coleccionResult.getResponse().getContentAsString());
        JsonNode primerEntrenador = root.get("_embedded").get("entrenadores").get(0);
        String selfUrl = primerEntrenador.get("_links").get("self").get("href").asText();
        assertNotNull(selfUrl);

        // 2. Navegar directamente utilizando la URL descubierta
        mockMvc.perform(get(selfUrl)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$._links.self", notNullValue()));
    }

    @Test
    @Order(10)
    @DisplayName("Prueba 10: Actualizar y enlaces dinámicos según estado -> HATEOAS adaptativo")
    void prueba10_actualizarYEstadoHateoas() throws Exception {
        String tokenAdmin = obtenerToken("admin", "admin123");

        // Entrenador 2 está en estado INACTIVO (debe tener el enlace 'activar')
        mockMvc.perform(get("/api/entrenadores/2")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("INACTIVO")))
                .andExpect(jsonPath("$._links.activar", notNullValue()));
    }

    @Test
    @Order(11)
    @DisplayName("Prueba 11: Consultar un identificador inexistente -> Se devuelve 404 Not Found")
    void prueba11_recursoInexistente() throws Exception {
        String token = obtenerToken("consulta", "consulta123");

        mockMvc.perform(get("/api/entrenadores/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("No se encontró el entrenador con ID: 999999")));
    }

    @Test
    @Order(12)
    @DisplayName("Prueba 12: Enviar datos inválidos al registrar -> Se devuelve 400 Bad Request con detalles")
    void prueba12_datosInvalidos() throws Exception {
        String tokenAdmin = obtenerToken("admin", "admin123");

        // Request con campos vacíos y correo inválido
        EntrenadorRequest requestInvalido = new EntrenadorRequest(
                "", // nombre vacío
                "", // apellido vacío
                "", // especialidad vacía
                -5, // años negativos inválidos
                "correo-invalido", // formato de correo inválido
                "123", // teléfono fuera de rango
                null
        );

        mockMvc.perform(post("/api/entrenadores")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details.nombre", notNullValue()))
                .andExpect(jsonPath("$.details.apellido", notNullValue()))
                .andExpect(jsonPath("$.details.especialidad", notNullValue()))
                .andExpect(jsonPath("$.details.experienciaAnios", notNullValue()))
                .andExpect(jsonPath("$.details.email", notNullValue()));
    }
}
