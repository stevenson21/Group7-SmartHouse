package com.fh.smartHouse;


import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SmartObjectsUI {

    private static List<SmartObject> smartObjects = new ArrayList<>();

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Manage Smart Objects");

        // ListView to display smart objects
        ListView<String> smartObjectList = new ListView<>();
        updateListView(smartObjectList);

        // Buttons for managing smart objects
        Button addButton = new Button("Add Smart Object");
        Button removeButton = new Button("Remove Selected");
        Button toggleButton = new Button("Toggle ON/OFF");
        Button markFaultyButton = new Button("Mark as Faulty");
        Button repairButton = new Button("Repair");
        Button viewDetailsButton = new Button("View Details");

        // Add functionality
        addButton.setOnAction(e -> addSmartObject(smartObjectList));

        // Remove functionality
        removeButton.setOnAction(e -> {
            int selectedIndex = smartObjectList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                smartObjects.remove(selectedIndex);
                updateListView(smartObjectList);
            }
        });

        // Toggle ON/OFF functionality
        toggleButton.setOnAction(e -> {
            int selectedIndex = smartObjectList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                SmartObject obj = smartObjects.get(selectedIndex);
                obj.toggleStatus();
                updateListView(smartObjectList);
            }
        });

        // Mark as faulty functionality
        markFaultyButton.setOnAction(e -> {
            int selectedIndex = smartObjectList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                SmartObject obj = smartObjects.get(selectedIndex);
                obj.markAsFaulty();
                updateListView(smartObjectList);
            }
        });

        // Repair functionality
        repairButton.setOnAction(e -> {
            int selectedIndex = smartObjectList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                SmartObject obj = smartObjects.get(selectedIndex);
                obj.repair();
                updateListView(smartObjectList);
            }
        });

        // View Details functionality
        viewDetailsButton.setOnAction(e -> {
            int selectedIndex = smartObjectList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                SmartObject selectedObject = smartObjects.get(selectedIndex);
                showDetails(selectedObject);
            }
        });

        VBox layout = new VBox(10, new Label("Smart Objects"), smartObjectList, addButton, removeButton, toggleButton, markFaultyButton, repairButton, viewDetailsButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(layout, 500, 500);
        window.setScene(scene);
        window.show();
    }

    private static void updateListView(ListView<String> listView) {
        listView.getItems().clear();
        for (SmartObject obj : smartObjects) {
            String status = obj.isOn() ? "ON" : "OFF";
            String faulty = obj.isFaulty() ? " (FAULTY)" : "";
            listView.getItems().add(obj.getName() + " - " + status + faulty);
        }
    }

    private static void addSmartObject(ListView<String> listView) {
        Stage inputWindow = new Stage();
        inputWindow.setTitle("Add Smart Object");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label consumptionLabel = new Label("Energy Consumption:");
        TextField consumptionField = new TextField();

        Button submitButton = new Button("Add");
        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            double consumption = Double.parseDouble(consumptionField.getText());
            smartObjects.add(new SmartObject(name, consumption));
            updateListView(listView);
            inputWindow.close();
        });

        VBox layout = new VBox(10, nameLabel, nameField, consumptionLabel, consumptionField, submitButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(layout, 300, 200);
        inputWindow.setScene(scene);
        inputWindow.show();
    }

    private static void showDetails(SmartObject smartObject) {
        Stage detailsWindow = new Stage();
        detailsWindow.setTitle("Smart Object Details - " + smartObject.getName());

        Label nameLabel = new Label("Name: " + smartObject.getName());
        Label consumptionLabel = new Label("Energy Consumption: " + smartObject.getEnergyConsumption() + " units");
        Label statusLabel = new Label("Status: " + (smartObject.isOn() ? "ON" : "OFF"));
        Label faultyLabel = new Label("Faulty: " + (smartObject.isFaulty() ? "Yes" : "No"));

        VBox layout = new VBox(10, nameLabel, consumptionLabel, statusLabel, faultyLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(layout, 400, 300);
        detailsWindow.setScene(scene);
        detailsWindow.show();
    }

    public static void initializeSmartObjects(List<SmartObject> objects) {
        smartObjects = objects;
    }
}
