module com.example.lab6 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.pdfbox;

    opens com.example.lab6 to javafx.fxml;
    exports com.example.lab6;
    exports com.example.lab6.controller;
    exports com.example.lab6.model;
    opens com.example.lab6.model;
    opens com.example.lab6.controller to javafx.fxml;
}