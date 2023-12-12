package org.application.ship_fx.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SeaGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/application/ship_fx/sea/sea-view.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Sea");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(e -> {
            try {
                stop();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    public static void main(String[] args) {
        launch();
    }
}
