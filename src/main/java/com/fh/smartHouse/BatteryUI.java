package com.fh.smartHouse;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BatteryUI {

    private static Battery battery;

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Battery Status");

        Label batteryStatusLabel = new Label("Battery Charge: " + battery.getCurrentCharge() + " units");

        VBox layout = new VBox(10, batteryStatusLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(layout, 300, 200);
        window.setScene(scene);
        window.show();
    }

    public static void setBattery(Battery battery) {
        BatteryUI.battery = battery;
    }
}
