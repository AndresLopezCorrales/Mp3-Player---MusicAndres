package com.example.musican;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException {

        //String cssFIle = this.getClass().getResource("/css/helloApplication.css").toExternalForm();
        //FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("hello-view.fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("principal.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        //scene.getStylesheets().add(cssFIle);

        Image logo = new Image(getClass().getResource("/images/MusciAllLogo.png").toExternalForm());
        stage.getIcons().add(logo);
        stage.setTitle("MusicAll");
        stage.setResizable(false);
        stage.setX(50);
        stage.setY(50);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}