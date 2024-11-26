package com.fh.smartHouse;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class SmartHouseUI extends Application {

    // Core components
    private static List<SmartObject> smartObjects;
    private static List<EnergySource> energySources;
    private static Battery battery;
    private static LogManager logManager;

    // Charts and visualization
    private static PieChart batteryChart;
    private static LineChart<Number, Number> energyUsageChart;
    private static LineChart<Number, Number> productionTrendsChart;
    private static XYChart.Series<Number, Number> usageSeries;
    private static List<XYChart.Series<Number, Number>> productionSeriesList;
    private int timeStep = 0;

    @Override
    public void start(Stage primaryStage) {
        // Initialize production trends chart before components
        productionTrendsChart = createProductionTrendsChart();

        initializeComponents(); // Initialize core components

        // Main layout
        VBox root = new VBox(10);

        // Buttons for managing functionalities
        Button manageSmartObjectsButton = new Button("Manage Smart Objects");
        manageSmartObjectsButton.setOnAction(e -> SmartObjectsUI.display());

        Button manageEnergySourcesButton = new Button("Manage Energy Sources");
        manageEnergySourcesButton.setOnAction(e -> EnergySourcesUI.display());

        Button viewBatteryStatusButton = new Button("View Battery Status");
        viewBatteryStatusButton.setOnAction(e -> BatteryUI.display());

        Button startSimulationButton = new Button("Start Energy Simulation");
        Button stopSimulationButton = new Button("Stop Simulation");

        // Battery chart for visualization
        batteryChart = new PieChart();
        updateBatteryChart();

        // Energy usage chart
        NumberAxis usageXAxis = new NumberAxis();
        usageXAxis.setLabel("Time (s)");
        NumberAxis usageYAxis = new NumberAxis();
        usageYAxis.setLabel("Energy Usage (units)");
        energyUsageChart = new LineChart<>(usageXAxis, usageYAxis);
        energyUsageChart.setTitle("Energy Usage Trends");
        usageSeries = new XYChart.Series<>();
        usageSeries.setName("Energy Usage");
        energyUsageChart.getData().add(usageSeries);

        // Timeline for energy simulation updates
        Timeline energySimulation = new Timeline(new KeyFrame(Duration.seconds(2), e -> updateSimulation()));
        energySimulation.setCycleCount(Timeline.INDEFINITE);

        startSimulationButton.setOnAction(e -> {
            energySimulation.play();
            logManager.logActivity("Energy simulation started.");
        });

        stopSimulationButton.setOnAction(e -> {
            energySimulation.stop();
            logManager.logActivity("Energy simulation stopped.");
        });

        // Add components to layout
        root.getChildren().addAll(manageSmartObjectsButton, manageEnergySourcesButton, viewBatteryStatusButton,
                batteryChart, energyUsageChart, productionTrendsChart, startSimulationButton, stopSimulationButton);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setTitle("Smart House Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeComponents() {
        // Initialize smart objects
        smartObjects = new ArrayList<>();
        smartObjects.add(new SmartObject("Refrigerator", 2.0));
        smartObjects.add(new SmartObject("Lighting System", 1.0));
        smartObjects.add(new SmartObject("Air Conditioner", 5.0));
        SmartObjectsUI.initializeSmartObjects(smartObjects);

        // Initialize energy sources
        energySources = new ArrayList<>();
        energySources.add(new EnergySource("Solar Panel", 10.0));
        energySources.add(new EnergySource("Wind Turbine", 15.0));
        energySources.add(new EnergySource("Hydro Generator", 20.0));
        EnergySourcesUI.initializeEnergySources(energySources);
        EnergySourcesUI.setSmartHouseUIReference(this); // Link to EnergySourcesUI

        // Initialize production series for energy sources
        productionSeriesList = new ArrayList<>();
        for (EnergySource source : energySources) {
            addProductionSeries(source);
        }

        // Initialize battery
        battery = new Battery(100.0, 3, energySources);
        BatteryUI.setBattery(battery);

        // Initialize log manager
        logManager = new LogManager("logs");
    }

    private LineChart<Number, Number> createProductionTrendsChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Energy Production (units)");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Energy Production Trends");
        return chart;
    }

    private void updateSimulation() {
        simulateEnergyFlow();

        // Update energy usage chart
        double totalUsage = smartObjects.stream()
                .filter(SmartObject::isOn)
                .mapToDouble(SmartObject::getEnergyConsumption)
                .sum();
        XYChart.Data<Number, Number> usageDataPoint = new XYChart.Data<>(timeStep, totalUsage);
        usageSeries.getData().add(usageDataPoint);

        // Add tooltip for usage data point
        usageDataPoint.nodeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Tooltip tooltip = new Tooltip("Time: " + timeStep + " s\nUsage: " + totalUsage + " units");
                Tooltip.install(usageDataPoint.getNode(), tooltip);
            }
        });

        // Update production trends chart
        for (int i = 0; i < energySources.size(); i++) {
            EnergySource source = energySources.get(i);
            if (i >= productionSeriesList.size()) {
                System.err.println("Mismatch between energy sources and production series list. Skipping index " + i);
                continue;
            }

            XYChart.Series<Number, Number> series = productionSeriesList.get(i);

            if (source.isEnabled() && !source.isFaulty()) {
                try {
                    double production = source.produceEnergy();
                    XYChart.Data<Number, Number> productionDataPoint = new XYChart.Data<>(timeStep, production);
                    series.getData().add(productionDataPoint);

                    // Add tooltip for production data point
                    productionDataPoint.nodeProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            Tooltip tooltip = new Tooltip("Source: " + source.getName() +
                                    "\nTime: " + timeStep + " s\nProduction: " + production + " units");
                            Tooltip.install(productionDataPoint.getNode(), tooltip);
                        }
                    });

                    // Limit the number of data points to keep the chart readable
                    if (series.getData().size() > 20) {
                        series.getData().remove(0);
                    }
                } catch (Exception e) {
                    System.err.println("Error during production for " + source.getName() + ": " + e.getMessage());
                }
            }
        }

        // Keep usage chart clean by limiting data points
        if (usageSeries.getData().size() > 20) {
            usageSeries.getData().remove(0);
        }

        timeStep++;
    }

    private void simulateEnergyFlow() {
        System.out.println("Simulating energy flow...");

        // Charge the battery from energy sources
        battery.chargeBattery();

        // Simulate energy usage by active smart objects
        double totalUsage = smartObjects.stream()
                .filter(SmartObject::isOn)
                .mapToDouble(SmartObject::getEnergyConsumption)
                .sum();

        try {
            battery.useEnergy(totalUsage);
        } catch (InsufficientChargeException | EMSUsageException e) {
            System.err.println("Energy usage failed: " + e.getMessage());
            logManager.logActivity("Energy usage failed: " + e.getMessage());
            return;
        }

        // Log battery charge
        logManager.logActivity("Simulation step: Battery charge is " + battery.getCurrentCharge() + " units.");

        // Update battery chart
        updateBatteryChart();
    }

    private void updateBatteryChart() {
        batteryChart.getData().clear();
        double charge = battery.getCurrentCharge();
        double capacity = 100.0; // Assuming battery capacity is 100 units

        // Battery chart slices
        PieChart.Data usedSlice = new PieChart.Data("Used", capacity - charge);
        PieChart.Data availableSlice = new PieChart.Data("Available", charge);

        batteryChart.getData().addAll(usedSlice, availableSlice);

        // Add tooltips after nodes are rendered
        batteryChart.getData().forEach(slice -> {
            Tooltip tooltip = new Tooltip(slice.getName() + ": " + slice.getPieValue() + " units");
            slice.getNode().setOnMouseEntered(e -> Tooltip.install(slice.getNode(), tooltip));
        });
    }

    public void addProductionSeries(EnergySource source) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(source.getName());
        productionSeriesList.add(series);
        productionTrendsChart.getData().add(series);
    }

    public void removeProductionSeries(EnergySource source) {
        for (int i = 0; i < productionSeriesList.size(); i++) {
            if (productionSeriesList.get(i).getName().equals(source.getName())) {
                productionTrendsChart.getData().remove(productionSeriesList.get(i));
                productionSeriesList.remove(i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
