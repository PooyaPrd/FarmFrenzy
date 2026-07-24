package com.farmfrenzy.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {

    @FXML
    private Button playButton;

    @FXML
    private Button exitButton;

    @FXML
    private void onPlay(ActionEvent event) {
    }

    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }
}
