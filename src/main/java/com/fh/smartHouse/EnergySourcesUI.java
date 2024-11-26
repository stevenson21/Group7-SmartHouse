package com.fh.smartHouse;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EnergySourcesUI {

    private static List<EnergySource> energySources = new ArrayList<>();
    private static SmartHouseUI smartHouseUIReference; // Reference to SmartHouseUI for syncing charts

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Manage Energy Sources");

        // ListView to display energy sources
        ListView<String> energySourceList = new ListView<>();
        updateListView(energySourceList);

        // Buttons for managing energy sources
        Button addButton = new Button("Add Energy Source");
        Button removeButton = new Button("Remove Selected");
        Button toggleButton = new Button("Enable/Disable");
        Button markFaultyButton = new Button("Mark as Faulty");
        Button repairButton = new Button("Repair");
        Button viewDetailsButton = new Button("View Details");

        // Add functionality
        addButton.setOnAction(e -> addEnergySource(energySourceList));

        // Remove functionality
        removeButton.setOnAction(e -> {
            int selectedIndex = energySourceList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                EnergySource removedSource = energySources.get(selectedIndex);
                energySources.remove(selectedIndex);
                updateListView(energySourceList);

                if (smartHouseUIReference != null) {
                    smartHouseUIReference.removeProductionSeries(removedSource); // Sync chart
                }
            }
        });

        // Toggle Enable/Disable functionality
        toggleButton.setOnAction(e -> {
            int selectedIndex = energySourceList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                EnergySource source = energySources.get(selectedIndex);
                try {
                    source.setEnabled(!source.isEnabled());
                    updateListView(energySourceList);
                } catch (EnergySourceException ex) {
                    showAlert("Error", ex.getMessage());
                }
            }
        });

        // Mark as faulty functionality
        markFaultyButton.setOnAction(e -> {
            int selectedIndex = energySourceList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                EnergySource source = energySources.get(selectedIndex);
                source.markAsFaulty();
                updateListView(energySourceList);
            }
        });

        // Repair functionality
        repairButton.setOnAction(e -> {
            int selectedIndex = energySourceList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                EnergySource source = energySources.get(selectedIndex);
                source.repair();
                updateListView(energySourceList);
            }
        });

        // View Details functionality
        viewDetailsButton.setOnAction(e -> {
            int selectedIndex = energySourceList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                EnergySource selectedSource = energySources.get(selectedIndex);
                showDetails(selectedSource);
            }
        });

        VBox layout = new VBox(10, new Label("Energy Sources"), energySourceList, addButton, removeButton,
                toggleButton, markFaultyButton, repairButton, viewDetailsButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(layout, 500, 500);
        window.setScene(scene);
        window.show();
    }

    private static void updateListView(ListView<String> listView) {
        listView.getItems().clear();
        for (EnergySource source : energySources) {
            String status = source.isEnabled() ? "ENABLED" : "DISABLED";
            String faulty = source.isFaulty() ? " (FAULTY)" : "";
            listView.getItems().add(source.getName() + " - " + status + faulty);
        }
    }

    private static void addEnergySource(ListView<String> listView) {
        Stage inputWindow = new Stage();
        inputWindow.setTitle("Add Energy Source");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label productionLabel = new Label("Energy Production Rate:");
        TextField productionField = new TextField();

        Button submitButton = new Button("Add");
        submitButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                double production = Double.parseDouble(productionField.getText());
                EnergySource newSource = new EnergySource(name, production);
                energySources.add(newSource);
                updateListView(listView);

                if (smartHouseUIReference != null) {
                    smartHouseUIReference.addProductionSeries(newSource); // Sync chart
                }

                inputWindow.close();
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid number for production rate.");
            }
        });

        VBox layout = new VBox(10, nameLabel, nameField, productionLabel, productionField, submitButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(layout, 300, 200);
        inputWindow.setScene(scene);
        inputWindow.show();
    }

    private static void showDetails(EnergySource energySource) {
        Stage detailsWindow = new Stage();
        detailsWindow.setTitle("Energy Source Details - " + energySource.getName());

        Label nameLabel = new Label("Name: " + energySource.getName());
        Label productionLabel = new Label("Production Rate: " + energySource.getEnergyProductionRate() + " units");
        Label statusLabel = new Label("Status: " + (energySource.isEnabled() ? "ENABLED" : "DISABLED"));
        Label faultyLabel = new Label("Faulty: " + (energySource.isFaulty() ? "Yes" : "No"));

        VBox layout = new VBox(10, nameLabel, productionLabel, statusLabel, faultyLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(layout, 400, 300);
        detailsWindow.setScene(scene);
        detailsWindow.show();
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void initializeEnergySources(List<EnergySource> sources) {
        energySources = sources;
    }

    public static void setSmartHouseUIReference(SmartHouseUI smartHouseUI) {
        smartHouseUIReference = smartHouseUI;
    }
}
