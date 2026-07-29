package com.farmfrenzy.controller;

import com.farmfrenzy.util.SceneSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class LevelSelectionController {

    public static int unlockedLevel = 1;

    @FXML
    private Button level1Button;

    @FXML
    private Button level2Button;

    @FXML
    private Button level3Button;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        lockButton(level2Button, 2);
        lockButton(level3Button, 3);
    }

    private void lockButton(Button button, int level) {
        if (level <= unlockedLevel) {
            return;
        }
        button.setDisable(true);
        InputStream stream = getClass().getResourceAsStream("/images/lock.png");
        if (stream == null) {
            return;
        }
        ImageView view = new ImageView(new Image(stream, 28, 28, true, true));
        view.setFitWidth(28);
        view.setFitHeight(28);
        button.setGraphic(view);
    }

    @FXML
    private void onLevelOne(ActionEvent event) {
        startLevel(1, level1Button);
    }

    @FXML
    private void onLevelTwo(ActionEvent event) {
        startLevel(2, level2Button);
    }

    @FXML
    private void onLevelThree(ActionEvent event) {
        startLevel(3, level3Button);
    }

    @FXML
    private void onBack(ActionEvent event) {
        SceneSwitcher.switchTo(backButton, "main_menu.fxml");
    }

    private void startLevel(int level, Button source) {
        GameStageController.selectedLevel = level;
        SceneSwitcher.switchTo(source, "game_stage.fxml");
    }
}
