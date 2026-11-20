
module com.nietosystems.frontendpersonas {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires tools.jackson.databind;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;


    opens com.nietosystems.frontendpersonas to javafx.fxml;
    exports com.nietosystems.frontendpersonas;

    opens com.nietosystems.frontendpersonas.controller to javafx.fxml;
    exports com.nietosystems.frontendpersonas.controller;
    opens com.nietosystems.frontendpersonas.model to javafx.base;
}
