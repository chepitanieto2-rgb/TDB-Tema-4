
package com.nietosystems.frontendpersonas.service;

import com.nietosystems.frontendpersonas.model.Persona;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class PersonaService {

    // Dirección de nuestra API Spring Boot
    private static final String API_URL =
            "http://localhost:8080/personas";

    // Cliente para realizar solicitudes HTTP
    private final HttpClient cliente =
            HttpClient.newHttpClient();

    // Convertidor de JSON
    private final ObjectMapper mapper =
            new ObjectMapper();

    // ========================================
    // POST - REGISTRAR PERSONA
    // ========================================

    public boolean registrarPersona(Persona persona)
            throws Exception {

        String json = String.format(
                "{\"nombre\":\"%s\","
                        + "\"apellidos\":\"%s\","
                        + "\"direccion\":\"%s\","
                        + "\"telefono\":\"%s\"}",
                escaparJson(persona.getNombre()),
                escaparJson(persona.getApellidos()),
                escaparJson(persona.getDireccion()),
                escaparJson(persona.getTelefono())
        );

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                json,
                                StandardCharsets.UTF_8
                        )
                )
                .build();

        HttpResponse<String> respuesta = cliente.send(
                solicitud,
                HttpResponse.BodyHandlers.ofString()
        );

        int codigo = respuesta.statusCode();

        if (codigo == 200 || codigo == 201) {
            return true;
        }

        throw new Exception(
                "Error HTTP " + codigo
                        + ": " + respuesta.body()
        );
    }

    // ========================================
    // GET - CONSULTAR PERSONAS
    // ========================================

    public List<Persona> obtenerPersonas()
            throws Exception {

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> respuesta = cliente.send(
                solicitud,
                HttpResponse.BodyHandlers.ofString()
        );

        if (respuesta.statusCode() != 200) {
            throw new Exception(
                    "Error HTTP "
                            + respuesta.statusCode()
                            + ": " + respuesta.body()
            );
        }

        JsonNode registros =
                mapper.readTree(respuesta.body());

        if (!registros.isArray()) {
            throw new Exception(
                    "La API no devolvió una lista de personas."
            );
        }

        List<Persona> personas = new ArrayList<>();

        for (JsonNode registro : registros) {

            Persona persona = new Persona();

            persona.setId(
                    registro.path("id").asLong()
            );

            persona.setNombre(
                    registro.path("nombre").asText()
            );

            persona.setApellidos(
                    registro.path("apellidos").asText()
            );

            persona.setDireccion(
                    registro.path("direccion").asText()
            );

            persona.setTelefono(
                    registro.path("telefono").asText()
            );

            personas.add(persona);
        }

        return personas;
    }

    // ========================================
    // MÉTODO AUXILIAR PARA JSON
    // ========================================

    private String escaparJson(String valor) {

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }


    public boolean actualizarPersona(Persona persona) throws Exception {

        String json = mapper.writeValueAsString(persona);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/" + persona.getId()))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(
                        json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> respuesta = cliente.send(
                solicitud,
                HttpResponse.BodyHandlers.ofString()
        );

        int codigo = respuesta.statusCode();

        if (codigo == 200 || codigo == 204) {
            return true;
        }

        throw new Exception(
                "Error HTTP " + codigo + ": " + respuesta.body()
        );
    }


    // ========================================
    // DELETE - ELIMINAR PERSONA
    // ========================================

    public boolean eliminarPersona(Long id) throws Exception {

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/" + id))
                .timeout(Duration.ofSeconds(10))
                .DELETE()
                .build();

        HttpResponse<String> respuesta = cliente.send(
                solicitud,
                HttpResponse.BodyHandlers.ofString()
        );

        int codigo = respuesta.statusCode();

        if (codigo == 200 || codigo == 204) {
            return true;
        }

        throw new Exception(
                "Error HTTP " + codigo + ": " + respuesta.body()
        );
    }


}
