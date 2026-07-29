package com.farmfrenzy.controller;

import com.farmfrenzy.MainApp;
import com.farmfrenzy.engine.GameEngine;
import com.farmfrenzy.exception.InsufficientCoinsException;
import com.farmfrenzy.exception.OutofWaterException;
import com.farmfrenzy.exception.WarehouseFullException;
import com.farmfrenzy.model.Grass;
import com.farmfrenzy.model.LevelConfig;
import com.farmfrenzy.model.base.Animal;
import com.farmfrenzy.model.base.Product;
import com.farmfrenzy.model.PlayerProgress;
import com.farmfrenzy.model.User;
import com.farmfrenzy.model.enums.AnimalState;
import com.farmfrenzy.model.enums.MachineState;
import com.farmfrenzy.repository.PlayerProgressRepository;
import com.farmfrenzy.util.SceneSwitcher;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.input.MouseEvent;
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
    private static final int CELL_WIDTH = 84;
    private static final int CELL_HEIGHT = 62;
    private static final int SPRITE_SIZE = 46;

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
    private ImageView wellImage;

    @FXML
    private ImageView warehouseImage;

    @FXML
    private ImageView machineImage;

    @FXML
    private Button pauseButton;

    private static final Map<String, Image> images = new HashMap<>();

    private static GameEngine activeGame;

    private GameEngine gameEngine;
    private LevelConfig levelConfig;
    private Timeline refreshLoop;
    private ScaleTransition machinePulse;
    private boolean levelFinished;

    @FXML
    public void initialize() {
        levelFinished = false;
        gameEngine = new GameEngine(selectedLevel);
        levelConfig = gameEngine.getLevelConfig();
        activeGame = gameEngine;
        gameEngine.setUpdateListener(() -> Platform.runLater(() -> onEngineUpdate()));
        objectiveLabel.setText(levelConfig.describeObjective());
        gameEngine.startGame();
        renderGrid();
        updateLabels();
        refreshLoop = new Timeline(new KeyFrame(Duration.millis(500), event -> refreshScreen()));
        refreshLoop.setCycleCount(Animation.INDEFINITE);
        refreshLoop.play();
        setupMachinePulse();
    }

    private void setupMachinePulse() {
        machinePulse = new ScaleTransition(Duration.millis(600), machineImage);
        machinePulse.setFromX(1.0);
        machinePulse.setFromY(1.0);
        machinePulse.setToX(1.1);
        machinePulse.setToY(1.1);
        machinePulse.setCycleCount(Animation.INDEFINITE);
        machinePulse.setAutoReverse(true);
    }

    private void updateMachinePulse() {
        boolean working = gameEngine.getEggPowderMachine().getState() == MachineState.WORKING;
        if (working) {
            if (machinePulse.getStatus() != Animation.Status.RUNNING) {
                machinePulse.playFromStart();
            }
        } else if (machinePulse.getStatus() == Animation.Status.RUNNING) {
            machinePulse.stop();
            machineImage.setScaleX(1.0);
            machineImage.setScaleY(1.0);
        }
    }

    private void onEngineUpdate() {
        updateLabels();
        checkLevelEnd();
    }

    private void refreshScreen() {
        renderGrid();
        updateLabels();
        updateMachinePulse();
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
                if (GameEngine.isPlantCell(col, row)) {
                    cell.getStyleClass().add("plant-cell");
                }
                cell.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
                final int cellX = col;
                final int cellY = row;
                cell.setOnMouseClicked(event -> onCellClicked(cellX, cellY));
                farmGrid.add(cell, col, row);
            }
        }
        for (Grass grass : gameEngine.getGrassList()) {
            addToCell(grass.getX(), grass.getY(), createNode("/images/grass.png", "Grass"));
        }
        for (Product product : gameEngine.getDroppedProducts()) {
            addToCell(product.getX(), product.getY(), createNode(product.getImagePath(), product.getName()));
        }
        for (Animal animal : gameEngine.getAnimals()) {
            addToCell(animal.getX(), animal.getY(), createNode(animalImage(animal), animal.getName()));
        }
    }

    private String animalImage(Animal animal) {
        if (animal.getState() == AnimalState.HUNGRY) {
            return "/images/chicken_hungry.png";
        }
        return animal.getImagePath();
    }

    private Node createNode(String imagePath, String fallbackText) {
        Image image = loadImage(imagePath);
        if (image != null) {
            ImageView view = new ImageView(image);
            view.setFitWidth(SPRITE_SIZE);
            view.setFitHeight(SPRITE_SIZE);
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
        Image image = new Image(stream, SPRITE_SIZE * 2, SPRITE_SIZE * 2, true, true);
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
        Product product = gameEngine.findProductAt(x, y);
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
            if (gameEngine.plantGrass(x, y)) {
                messageLabel.setText("Grass planted");
            } else {
                messageLabel.setText("You cannot plant grass here");
            }
        } catch (OutofWaterException e) {
            showError(e.getMessage());
        }
        renderGrid();
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
    private void onWellClicked(MouseEvent event) {
        try {
            gameEngine.refillWell();
            messageLabel.setText("The well is full again");
        } catch (InsufficientCoinsException e) {
            showError(e.getMessage());
        }
        updateLabels();
    }

    @FXML
    private void onWarehouseClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/warehouse_dialog.fxml"));
            Parent root = loader.load();
            WarehouseDialogController controller = loader.getController();
            controller.setGameEngine(gameEngine);
            Stage dialog = new Stage();
            dialog.setTitle("Warehouse");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            MainApp.setAppIcon(dialog);
            dialog.showAndWait();
            updateLabels();
        } catch (IOException e) {
            showError("Could not open the warehouse");
        }
    }

    @FXML
    private void onMachineClicked(MouseEvent event) {
        gameEngine.startEggPowderMachine();
        updateMachinePulse();
        messageLabel.setText("Egg powder machine started");
    }

    @FXML
    private void onPauseClicked(ActionEvent event) {
        stopEverything();
        SceneSwitcher.switchTo(pauseButton, "login.fxml");
    }

    private void checkLevelEnd() {
        if (levelFinished || !isObjectiveDone()) {
            return;
        }
        levelFinished = true;
        int seconds = gameEngine.getLevelTime();
        int earned = gameEngine.getCoins();
        stopEverything();
        if (selectedLevel + 1 > LevelSelectionController.unlockedLevel) {
            LevelSelectionController.unlockedLevel = selectedLevel + 1;
        }
        saveProgress(earned);
        LevelScoreController.stars = calculateStars(seconds);
        LevelScoreController.coinsEarned = earned;
        LevelScoreController.timeTaken = seconds;
        SceneSwitcher.switchTo(farmGrid, "level_score.fxml");
    }

    private boolean isObjectiveDone() {
        if (levelConfig.isProductionLevel()) {
            return gameEngine.countAliveChickens() >= levelConfig.getChickenGoal()
                    && gameEngine.countEggPowderInWarehouse() >= levelConfig.getPowderGoal();
        }
        return gameEngine.getCoins() >= levelConfig.getCoinGoal();
    }

    private void saveProgress(int earnedCoins) {
        User player = LoginController.currentUser;
        if (player == null) {
            return;
        }
        PlayerProgressRepository progressRepository = new PlayerProgressRepository();
        PlayerProgress saved = progressRepository.getProgressByUserId(player.getId());
        int bestLevel = selectedLevel;
        if (saved != null && saved.getLevel() > bestLevel) {
            bestLevel = saved.getLevel();
        }
        progressRepository.saveProgress(player.getId(), bestLevel, earnedCoins);
    }

    private int calculateStars(int seconds) {
        if (seconds <= levelConfig.getGoldTime()) {
            return 3;
        }
        if (seconds <= levelConfig.getSilverTime()) {
            return 2;
        }
        return 1;
    }

    private void stopEverything() {
        if (refreshLoop != null) {
            refreshLoop.stop();
        }
        if (machinePulse != null) {
            machinePulse.stop();
        }
        gameEngine.setUpdateListener(null);
        gameEngine.stopGame();
        activeGame = null;
    }

    public static void shutdownActiveGame() {
        if (activeGame != null) {
            activeGame.setUpdateListener(null);
            activeGame.stopGame();
            activeGame = null;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Farm Frenzy 2");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
