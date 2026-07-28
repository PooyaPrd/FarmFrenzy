package com.farmfrenzy.controller;

import com.farmfrenzy.engine.GameEngine;
import com.farmfrenzy.exception.InsufficientCoinsException;
import com.farmfrenzy.exception.OutofWaterException;
import com.farmfrenzy.exception.WarehouseFullException;
import com.farmfrenzy.model.EggPowderMachine;
import com.farmfrenzy.model.Grass;
import com.farmfrenzy.model.base.Animal;
import com.farmfrenzy.model.base.Product;
import com.farmfrenzy.model.enums.AnimalState;
import com.farmfrenzy.util.SceneSwitcher;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GameStageController {

    public static int selectedLevel = 1;

    private static final int ROWS = 5;
    private static final int COLS = 6;

    @FXML
    private GridPane farmGrid;

    @FXML
    private Label coinsLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label waterLabel;

    @FXML
    private Label warehouseLabel;

    @FXML
    private Label objectiveLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button buyChickenButton;

    @FXML
    private Button wellButton;

    @FXML
    private Button warehouseButton;

    @FXML
    private Button machineButton;

    @FXML
    private Button pauseButton;

    private static final Map<String, Image> images = new HashMap<>();

    private GameEngine gameEngine;
    private Timeline refreshLoop;
    private int requiredCoins;
    private boolean levelFinished;

    @FXML
    public void initialize() {
        requiredCoins = requiredCoinsForLevel(selectedLevel);
        levelFinished = false;
        gameEngine = new GameEngine(60);
        gameEngine.setUiUpdate(this::updateLabels);
        objectiveLabel.setText("Objective: earn " + requiredCoins + " coins");
        gameEngine.startGame();
        renderGrid();
        updateLabels();
        refreshLoop = new Timeline(new KeyFrame(Duration.millis(500), event -> refreshScreen()));
        refreshLoop.setCycleCount(Animation.INDEFINITE);
        refreshLoop.play();
    }

    private int requiredCoinsForLevel(int level) {
        if (level == 1) {
            return 150;
        }
        if (level == 2) {
            return 250;
        }
        return 400;
    }

    private void refreshScreen() {
        renderGrid();
        updateLabels();
        checkLevelEnd();
    }

    private void updateLabels() {
        coinsLabel.setText("Coins: " + gameEngine.getCoins());
        timerLabel.setText("Time: " + gameEngine.getLevelTime());
        waterLabel.setText("Water: " + gameEngine.getWaterWell().getCurrentWater()
                + " / " + gameEngine.getWaterWell().getMaxWater());
        warehouseLabel.setText("Warehouse: " + gameEngine.getWarehouse().getCurrentVolume()
                + " / " + gameEngine.getWarehouse().getCapacity());
    }

    private void renderGrid() {
        farmGrid.getChildren().clear();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("grid-cell");
                cell.setPrefSize(90, 80);
                final int cellX = col;
                final int cellY = row;
                cell.setOnMouseClicked(event -> onCellClicked(cellX, cellY));
                farmGrid.add(cell, col, row);
            }
        }
        for (Grass grass : gameEngine.getGrassList()) {
            addToCell(grass.getX(), grass.getY(), createNode("/images/grass.jpg", "Grass"));
        }
        EggPowderMachine machine = gameEngine.getEggPowderMachine();
        addToCell(machine.getX(), machine.getY(), createNode(machine.getImagePath(), machine.getName()));
        for (Product product : gameEngine.getDroppedProducts()) {
            addToCell(product.getX(), product.getY(), createNode(product.getImagePath(), product.getName()));
        }
        for (Animal animal : gameEngine.getAnimals()) {
            addToCell(animal.getX(), animal.getY(), createNode(animalImage(animal), animal.getName()));
        }
    }

    private String animalImage(Animal animal) {
        if (animal.getState() == AnimalState.HUNGRY) {
            return "/images/chicken_hungry.jpg";
        }
        return animal.getImagePath();
    }

    private Node createNode(String imagePath, String fallbackText) {
        Image image = loadImage(imagePath);
        if (image != null) {
            ImageView view = new ImageView(image);
            view.setFitWidth(50);
            view.setFitHeight(50);
            view.setPreserveRatio(true);
            return view;
        }
        Label label = new Label(fallbackText);
        label.getStyleClass().add("info-label");
        return label;
    }

    private Image loadImage(String path) {
        if (images.containsKey(path)) {
            return images.get(path);
        }
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            images.put(path, null);
            return null;
        }
        Image image = new Image(stream, 50, 50, true, true);
        images.put(path, image);
        return image;
    }

    private void addToCell(int x, int y, Node node) {
        for (Node child : farmGrid.getChildren()) {
            Integer column = GridPane.getColumnIndex(child);
            Integer row = GridPane.getRowIndex(child);
            if (column != null && row != null && column == x && row == y) {
                ((StackPane) child).getChildren().add(node);
                return;
            }
        }
    }

    private void onCellClicked(int x, int y) {
        Product product = findProductAt(x, y);
        if (product != null) {
            try {
                gameEngine.collectProduct(product);
                messageLabel.setText("Collected " + product.getName());
            } catch (WarehouseFullException e) {
                showError(e.getMessage());
            }
            return;
        }
        try {
            gameEngine.plantGrass(x, y);
            messageLabel.setText("Grass planted");
        } catch (OutofWaterException e) {
            showError(e.getMessage());
        }
        renderGrid();
    }

    private Product findProductAt(int x, int y) {
        for (Product product : gameEngine.getDroppedProducts()) {
            if (product.getX() == x && product.getY() == y) {
                return product;
            }
        }
        return null;
    }

    @FXML
    private void onBuyChickenClicked(ActionEvent event) {
        try {
            gameEngine.buyChicken();
            messageLabel.setText("A new chicken joined the farm");
        } catch (InsufficientCoinsException e) {
            showError(e.getMessage());
        }
        renderGrid();
        updateLabels();
    }

    @FXML
    private void onWellClicked(ActionEvent event) {
        try {
            gameEngine.refillWell();
            messageLabel.setText("The well is full again");
        } catch (InsufficientCoinsException e) {
            showError(e.getMessage());
        }
        updateLabels();
    }

    @FXML
    private void onWarehouseClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/warehouse_dialog.fxml"));
            Parent root = loader.load();
            WarehouseDialogController controller = loader.getController();
            controller.setGameEngine(gameEngine);
            Stage dialog = new Stage();
            dialog.setTitle("Warehouse");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
            updateLabels();
        } catch (IOException e) {
            showError("Could not open the warehouse");
        }
    }

    @FXML
    private void onMachineClicked(ActionEvent event) {
        gameEngine.startEggPowderMachine();
        messageLabel.setText("Egg powder machine started");
    }

    @FXML
    private void onPauseClicked(ActionEvent event) {
        stopEverything();
        SceneSwitcher.switchTo(pauseButton, "main_menu.fxml");
    }

    private void checkLevelEnd() {
        if (levelFinished || gameEngine.getCoins() < requiredCoins) {
            return;
        }
        levelFinished = true;
        int seconds = gameEngine.getLevelTime();
        int earned = gameEngine.getCoins();
        stopEverything();
        if (selectedLevel + 1 > LevelSelectionController.unlockedLevel) {
            LevelSelectionController.unlockedLevel = selectedLevel + 1;
        }
        LevelScoreController.stars = calculateStars(seconds);
        LevelScoreController.coinsEarned = earned;
        LevelScoreController.timeTaken = seconds;
        SceneSwitcher.switchTo(farmGrid, "level_score.fxml");
    }

    private int calculateStars(int seconds) {
        if (seconds <= 120) {
            return 3;
        }
        if (seconds <= 240) {
            return 2;
        }
        return 1;
    }

    private void stopEverything() {
        if (refreshLoop != null) {
            refreshLoop.stop();
        }
        gameEngine.setUiUpdate(null);
        gameEngine.stopGame();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Farm Frenzy 2");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
