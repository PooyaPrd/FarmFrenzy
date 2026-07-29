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
import javafx.scene.layout.VBox;

public class LoginController {

    public static User currentUser;

    @FXML
    private VBox loginBox;

    @FXML
    private VBox menuBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button startButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Label welcomeLabel;

    private UserRepository userRepository = new UserRepository();
    private PlayerProgressRepository progressRepository = new PlayerProgressRepository();

    @FXML
    public void initialize() {
        if (currentUser != null) {
            showMenu();
        } else {
            showLogin();
        }
    }

    private void showMenu() {
        welcomeLabel.setText("Welcome " + currentUser.getUsername());
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        menuBox.setVisible(true);
        menuBox.setManaged(true);
    }

    private void showLogin() {
        messageLabel.setText("");
        usernameField.clear();
        passwordField.clear();
        menuBox.setVisible(false);
        menuBox.setManaged(false);
        loginBox.setVisible(true);
        loginBox.setManaged(true);
    }

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
        showMenu();
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
        showMenu();
    }

    @FXML
    private void onStartGame(ActionEvent event) {
        SceneSwitcher.switchTo(startButton, "level_selection.fxml");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        currentUser = null;
        LevelSelectionController.unlockedLevel = 1;
        showLogin();
    }

    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }
}
