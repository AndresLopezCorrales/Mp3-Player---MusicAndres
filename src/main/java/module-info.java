module com.example.musican {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires javafx.media;

    opens com.example.musican to javafx.fxml;
    opens com.example.musican.controller to javafx.fxml;
    exports com.example.musican;
}