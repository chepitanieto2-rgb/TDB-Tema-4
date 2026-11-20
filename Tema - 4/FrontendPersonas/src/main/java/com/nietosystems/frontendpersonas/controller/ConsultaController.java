
package com.nietosystems.frontendpersonas.controller;

import com.nietosystems.frontendpersonas.model.Persona;
import com.nietosystems.frontendpersonas.service.PersonaService;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;
import java.util.List;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;
import javafx.scene.layout.HBox;

public class ConsultaController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Persona> tablaPersonas;

    @FXML private TableColumn<Persona, Long> colId;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellidos;
    @FXML private TableColumn<Persona, String> colDireccion;
    @FXML private TableColumn<Persona, String> colTelefono;
    @FXML private TableColumn<Persona, Void> colAcciones;

    @FXML private Label lblTotal;

    private final PersonaService servicio = new PersonaService();
    private final ObservableList<Persona> listaPersonas =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colApellidos.setCellValueFactory(
                new PropertyValueFactory<>("apellidos"));

        colDireccion.setCellValueFactory(
                new PropertyValueFactory<>("direccion"));

        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono"));

        lblTotal.setText("0");


        colAcciones.setCellFactory(columna -> new TableCell<Persona, Void>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            private final HBox botones = new HBox(8, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    Persona persona = getTableView()
                            .getItems()
                            .get(getIndex());

                    editarPersona(persona);
                });

                btnEliminar.setOnAction(event -> {
                    Persona persona = getTableView()
                            .getItems()
                            .get(getIndex());

                    eliminarPersona(persona);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(botones);
                }
            }
        });


        cargarPersonas();
    }

    private void cargarPersonas() {

        Task<List<Persona>> tarea = new Task<>() {
            @Override
            protected List<Persona> call() throws Exception {
                return servicio.obtenerPersonas();
            }
        };

        tarea.setOnSucceeded(event -> {

            List<Persona> personas = tarea.getValue();

            listaPersonas.setAll(personas);
            tablaPersonas.setItems(listaPersonas);

            lblTotal.setText(
                    String.valueOf(personas.size())
            );
        });

        tarea.setOnFailed(event -> {

            Throwable error = tarea.getException();

            mostrarMensaje(
                    "Error de consulta",
                    "No se pudieron cargar las personas: "
                            + error.getMessage()
            );
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    @FXML
    private void actualizarTabla() {
        cargarPersonas();
    }

    @FXML
    private void buscarPersona() {

        String busqueda = txtBuscar.getText().trim().toLowerCase();

        FilteredList<Persona> filtrados =
                new FilteredList<>(listaPersonas, persona -> {

                    if (busqueda.isEmpty()) {
                        return true;
                    }

                    return String.valueOf(persona.getId()).contains(busqueda)
                            || persona.getNombre().toLowerCase().contains(busqueda)
                            || persona.getApellidos().toLowerCase().contains(busqueda);
                });

        tablaPersonas.setItems(filtrados);
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


    private void editarPersona(Persona persona) {

        Dialog<ButtonType> dialogo = new Dialog<>();

        dialogo.setTitle("Editar persona");
        dialogo.setHeaderText(
                "Modificar datos de la persona con ID: " + persona.getId()
        );

        // Campos con los datos actuales
        TextField txtNombre = new TextField(persona.getNombre());
        TextField txtApellidos = new TextField(persona.getApellidos());
        TextField txtDireccion = new TextField(persona.getDireccion());
        TextField txtTelefono = new TextField(persona.getTelefono());

        // Organización del formulario
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(20));

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(txtNombre, 1, 0);

        formulario.add(new Label("Apellidos:"), 0, 1);
        formulario.add(txtApellidos, 1, 1);

        formulario.add(new Label("Dirección:"), 0, 2);
        formulario.add(txtDireccion, 1, 2);

        formulario.add(new Label("Teléfono:"), 0, 3);
        formulario.add(txtTelefono, 1, 3);

        dialogo.getDialogPane().setContent(formulario);

        dialogo.getDialogPane().getButtonTypes().addAll(
                ButtonType.CANCEL,
                new ButtonType("Guardar cambios", ButtonBar.ButtonData.OK_DONE)
        );

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent()
                && resultado.get().getButtonData()
                == ButtonBar.ButtonData.OK_DONE) {


            // Validar que ningún campo esté vacío
            if (txtNombre.getText().isBlank()
                    || txtApellidos.getText().isBlank()
                    || txtDireccion.getText().isBlank()
                    || txtTelefono.getText().isBlank()) {

                mostrarMensaje(
                        "Campos obligatorios",
                        "Todos los campos deben estar completos."
                );
                return;
            }

            // Validar que el teléfono contenga solo números
            String telefono = txtTelefono.getText().trim();

            if (!telefono.matches("[0-9]+")) {

                mostrarMensaje(
                        "Teléfono inválido",
                        "El número de teléfono debe contener únicamente números."
                );
                return;
            }


            // Crear un objeto con los datos modificados
            Persona personaActualizada = new Persona();

            personaActualizada.setId(persona.getId());
            personaActualizada.setNombre(txtNombre.getText().trim());
            personaActualizada.setApellidos(txtApellidos.getText().trim());
            personaActualizada.setDireccion(txtDireccion.getText().trim());
            personaActualizada.setTelefono(txtTelefono.getText().trim());

            // Enviar los cambios a Spring Boot
            Task<Boolean> tarea = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return servicio.actualizarPersona(personaActualizada);
                }
            };

            tarea.setOnSucceeded(event -> {
                if (Boolean.TRUE.equals(tarea.getValue())) {
                    mostrarMensaje(
                            "Actualización exitosa",
                            "Los datos se actualizaron correctamente."
                    );
                    cargarPersonas();
                }
            });

            tarea.setOnFailed(event -> {
                mostrarMensaje(
                        "Error al actualizar",
                        tarea.getException().getMessage()
                );
            });

            Thread hilo = new Thread(tarea);
            hilo.setDaemon(true);
            hilo.start();
        }
    }


    private void eliminarPersona(Persona persona) {

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Eliminar persona");
        confirmacion.setHeaderText("Confirmar eliminación");

        confirmacion.setContentText(
                "¿Estás segura de eliminar a "
                        + persona.getNombre() + " "
                        + persona.getApellidos() + "?\n\n"
                        + "Esta acción no se puede deshacer."
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            Task<Boolean> tarea = new Task<>() {

                @Override
                protected Boolean call() throws Exception {
                    return servicio.eliminarPersona(persona.getId());
                }
            };

            tarea.setOnSucceeded(event -> {

                if (Boolean.TRUE.equals(tarea.getValue())) {

                    mostrarMensaje(
                            "Eliminación exitosa",
                            "La persona fue eliminada correctamente."
                    );

                    cargarPersonas();
                }
            });

            tarea.setOnFailed(event -> {

                mostrarMensaje(
                        "Error al eliminar",
                        tarea.getException().getMessage()
                );
            });

            Thread hilo = new Thread(tarea);
            hilo.setDaemon(true);
            hilo.start();
        }
    }


}
