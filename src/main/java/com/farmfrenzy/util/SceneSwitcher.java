package com.farmfrenzy.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchTo(Node source, String fxmlName) {
        try {
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource("/fxml/" + fxmlName));
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            System.out.println("Could not open " + fxmlName + ": " + e.getMessage());
        }
    }
}
