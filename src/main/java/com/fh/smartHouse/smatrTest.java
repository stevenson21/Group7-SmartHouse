package com.fh.smartHouse;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class smatrTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Pie chart for energy consumption
        PieChart energyChart = new PieChart();
        energyChart.getData().add(new PieChart.Data("Refrigerator", 30));
        energyChart.getData().add(new PieChart.Data("Lighting", 20));
        energyChart.getData().add(new PieChart.Data("Air Conditioning", 50));

        // Button to simulate energy usage
        Button simulateButton = new Button("Simulate Energy Usage");
        simulateButton.setOnAction(e -> {
            System.out.println("Simulating energy usage...");
            energyChart.getData().clear();
            energyChart.getData().add(new PieChart.Data("Refrigerator", 40));
            energyChart.getData().add(new PieChart.Data("Lighting", 30));
            energyChart.getData().add(new PieChart.Data("Air Conditioning", 60));
        });

        // Layout
        VBox layout = new VBox(10, energyChart, simulateButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");

        Scene scene = new Scene(layout, 600, 400);

        // Setup Stage
        primaryStage.setTitle("Smart House Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

