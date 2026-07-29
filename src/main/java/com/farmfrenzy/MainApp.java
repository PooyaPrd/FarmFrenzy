package com.farmfrenzy;

import com.farmfrenzy.controller.GameStageController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Farm Frenzy 2");
        stage.setScene(scene);
        stage.setResizable(false);
        setAppIcon(stage);
        stage.show();
    }

    public static void setAppIcon(Stage stage) {
        InputStream stream = MainApp.class.getResourceAsStream("/images/app_icon.jpg");
        if (stream == null) {
            return;
        }
        stage.getIcons().add(new Image(stream));
    }

    @Override
    public void stop() {
        GameStageController.shutdownActiveGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
