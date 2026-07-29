package com.farmfrenzy.controller;

import com.farmfrenzy.model.PlayerProgress;
import com.farmfrenzy.model.User;
import com.farmfrenzy.repository.PlayerProgressRepository;
import com.farmfrenzy.repository.UserRepository;
import com.farmfrenzy.util.SceneSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    public static User currentUser;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label messageLabel;

    private UserRepository userRepository = new UserRepository();
    private PlayerProgressRepository progressRepository = new PlayerProgressRepository();

    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill both fields");
            return;
        }
        if (!userRepository.validateUser(username, password)) {
            messageLabel.setText("Wrong username or password");
            return;
        }
        currentUser = userRepository.getUser(username);
        loadProgress();
        SceneSwitcher.switchTo(loginButton, "main_menu.fxml");
    }

    private void loadProgress() {
        LevelSelectionController.unlockedLevel = 1;
        if (currentUser == null) {
            return;
        }
        PlayerProgress progress = progressRepository.getProgressByUserId(currentUser.getId());
        if (progress != null) {
            LevelSelectionController.unlockedLevel = progress.getLevel() + 1;
        }
    }

    @FXML
    private void onRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill both fields");
            return;
        }
        if (userRepository.getUser(username) != null) {
            messageLabel.setText("This username is already taken");
            return;
        }
        if (!userRepository.saveUser(username, password)) {
            messageLabel.setText("Could not create the account");
            return;
        }
        currentUser = userRepository.getUser(username);
        LevelSelectionController.unlockedLevel = 1;
        SceneSwitcher.switchTo(registerButton, "main_menu.fxml");
    }
}
