
package com.nietosystems.frontendpersonas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contenedorPrincipal;

    @FXML
    public void initialize() {
        mostrarInicio();
    }

    private void cargarVista(String archivoFXML) {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/nietosystems/frontendpersonas/"
                                    + archivoFXML
                    )
            );

            Parent vista = loader.load();

            contenedorPrincipal.getChildren().setAll(vista);

        } catch (IOException e) {

            Alert alerta = new Alert(
                    Alert.AlertType.ERROR
            );

            alerta.setTitle("Error");
            alerta.setHeaderText(
                    "No se pudo cargar la vista"
            );
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    private void mostrarInicio() {
        cargarVista("inicio-view.fxml");
    }

    @FXML
    private void registrarPersona() {
        cargarVista("registro-view.fxml");
    }


    @FXML
    private void consultarPersonas() {
        cargarVista("consulta-view.fxml");
    }


    @FXML
    private void administrarSistema() {
        mostrarMensaje(
                "Próximamente: Administración del sistema"
        );
    }

    private void mostrarMensaje(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Sistema de Personas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
