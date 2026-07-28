package com.farmfrenzy.controller;

import com.farmfrenzy.engine.GameEngine;
import com.farmfrenzy.model.base.Product;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class WarehouseDialogController {

    @FXML
    private ListView<String> productListView;

    @FXML
    private Label capacityLabel;

    @FXML
    private Button sellButton;

    @FXML
    private Button closeButton;

    private GameEngine gameEngine;
    private List<Product> items = new ArrayList<>();

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        refreshList();
    }

    private void refreshList() {
        if (gameEngine == null) {
            return;
        }
        items = gameEngine.getWarehouse().getProducts();
        productListView.getItems().clear();
        for (Product p : items) {
            productListView.getItems().add(p.getName() + " - " + p.getPrice() + " coins");
        }
        capacityLabel.setText("Space: " + gameEngine.getWarehouse().getCurrentVolume()
                + " / " + gameEngine.getWarehouse().getCapacity());
    }

    @FXML
    private void onSell(ActionEvent event) {
        int index = productListView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= items.size()) {
            return;
        }
        Product selected = items.get(index);
        gameEngine.sellProductFromWarehouse(selected);
        refreshList();
    }

    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
