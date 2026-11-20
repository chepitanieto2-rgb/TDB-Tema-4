
package com.nietosystems.frontendpersonas.controller;

import com.nietosystems.frontendpersonas.model.Persona;
import com.nietosystems.frontendpersonas.service.PersonaService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class RegistroController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;

    private final PersonaService servicio =
            new PersonaService();

    @FXML
    private void guardarPersona() {

        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();

        //Validar campos vacios
        if (nombre.isEmpty() || apellidos.isEmpty()
                || direccion.isEmpty() || telefono.isEmpty()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Campos incompletos",
                    "Debe completar todos los campos."
            );
            return;
        }

        // Validar que el telefono contenga unicamente numeros
        if (!telefono.matches("[0-9]+")) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Teléfono inválido",
                    "El número de teléfono debe contener únicamente números."
            );
            return;
        }

        Persona persona = new Persona(
                nombre, apellidos, direccion, telefono
        );

        Task<Boolean> tarea = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return servicio.registrarPersona(persona);
            }
        };

        tarea.setOnSucceeded(event -> {
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "La persona fue registrada correctamente."
            );
            limpiarCampos();
        });

        tarea.setOnFailed(event -> {
            Throwable error = tarea.getException();

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de registro",
                    "No se pudo registrar la persona: "
                            + error.getMessage()
            );
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellidos.clear();
        txtDireccion.clear();
        txtTelefono.clear();
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
