package com.fh.smartHouse;

public class SmartObject {
    private String name; // Name of the smart object
    private double energyConsumptionRate; // Energy consumption rate in kWh
    private boolean isOn; // Current status (ON/OFF)
    private boolean isFaulty; // Indicates if the object has a fault

    public SmartObject(String name, double energyConsumptionRate) {
        this.name = name;
        this.energyConsumptionRate = energyConsumptionRate;
        this.isOn = false; // Default to OFF
        this.isFaulty = false; // Default to no fault
    }

    // Toggle the status of the smart object
    public void toggleStatus() {
        if (isFaulty) {
            System.err.println(name + " is faulty and cannot be toggled.");
            return;
        }
        isOn = !isOn;
        System.out.println(name + " is now " + (isOn ? "ON" : "OFF"));
    }

    // Simulate energy consumption by the smart object
    public double getEnergyConsumption() {
        if (isFaulty) {
            System.err.println(name + " is faulty and cannot consume energy.");
            return 0.0; // Default value when faulty
        }
        return isOn ? energyConsumptionRate : 0.0;
    }

    // Introduce a fault in the smart object
    public void markAsFaulty() {
        isFaulty = true;
        System.err.println(name + " has developed a fault.");
    }

    // Repair the smart object
    public void repair() {
        isFaulty = false;
        System.out.println(name + " has been repaired and is operational.");
    }

    // Get the name of the smart object
    public String getName() {
        return name;
    }

    // Get the status of the smart object
    public boolean isOn() {
        return isOn;
    }

    // Check if the smart object is faulty
    public boolean isFaulty() {
        return isFaulty;
    }
}

