package com.farmfrenzy.controller;

import com.farmfrenzy.util.SceneSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.InputStream;

public class LevelScoreController {

    public static int stars = 0;
    public static int coinsEarned = 0;
    public static int timeTaken = 0;

    @FXML
    private Label starsLabel;

    @FXML
    private Label coinsLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private HBox starsBox;

    @FXML
    private Button okButton;

    @FXML
    public void initialize() {
        starsLabel.setText("Stars: " + stars);
        coinsLabel.setText("Coins Earned: " + coinsEarned);
        timeLabel.setText("Time Taken: " + timeTaken + " seconds");
        showStars();
    }

    private void showStars() {
        starsBox.getChildren().clear();
        for (int i = 0; i < stars; i++) {
            InputStream stream = getClass().getResourceAsStream("/images/star.jpg");
            if (stream == null) {
                return;
            }
            ImageView view = new ImageView(new Image(stream, 60, 60, true, true));
            view.setFitWidth(60);
            view.setFitHeight(60);
            starsBox.getChildren().add(view);
        }
    }

    @FXML
    private void onOk(ActionEvent event) {
        SceneSwitcher.switchTo(okButton, "level_selection.fxml");
    }
}
