package com.farmfrenzy.controller;

import com.farmfrenzy.util.SceneSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML
    private Button startButton;

    @FXML
    private Button levelButton;

    @FXML
    private Button exitButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        if (LoginController.currentUser != null) {
            welcomeLabel.setText("Welcome " + LoginController.currentUser.getUsername());
        }
    }

    @FXML
    private void onStartGame(ActionEvent event) {
        GameStageController.selectedLevel = 1;
        SceneSwitcher.switchTo(startButton, "game_stage.fxml");
    }

    @FXML
    private void onLevelSelection(ActionEvent event) {
        SceneSwitcher.switchTo(levelButton, "level_selection.fxml");
    }

    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }
}
