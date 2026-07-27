package com.farmfrenzy.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {

    @FXML
    private Button startButton;

    @FXML
    private Button levelButton;

    @FXML
    private Button exitButton;

    @FXML
    private void onStartGame(ActionEvent event) {
    }

    @FXML
    private void onLevelSelection(ActionEvent event) {
    }

    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }
}
