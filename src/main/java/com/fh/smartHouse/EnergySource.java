package com.fh.smartHouse;

public class EnergySource {
    private String name; // Name of the energy source
    private double energyProductionRate; // Energy production rate in kWh
    private boolean isEnabled; // Current status (enabled/disabled)
    private boolean isFaulty; // Indicates if the energy source is faulty

    public EnergySource(String name, double energyProductionRate) {
        this.name = name;
        this.energyProductionRate = energyProductionRate;
        this.isEnabled = true; // Default to enabled
        this.isFaulty = false; // Default to no fault
    }

    // Enable or disable the energy source
    public void setEnabled(boolean isEnabled) throws EnergySourceException {
        if (isFaulty) {
            throw new EnergySourceException(name + " is faulty and cannot be enabled or disabled.");
        }

        this.isEnabled = isEnabled;
        System.out.println(name + " is now " + (isEnabled ? "ENABLED" : "DISABLED"));
    }

    // Simulate energy production
    public double produceEnergy() throws EnergySourceException {
        if (!isEnabled) {
            throw new EnergySourceException(name + " is disabled and cannot produce energy.");
        }

        if (isFaulty) {
            throw new EnergySourceException(name + " is faulty and cannot produce energy.");
        }

        System.out.println(name + " produced " + energyProductionRate + " kWh.");
        return energyProductionRate;
    }

    // Introduce a fault in the energy source
    public void markAsFaulty() {
        isFaulty = true;
        System.err.println(name + " has developed a fault.");
    }

    // Repair the energy source
    public void repair() {
        isFaulty = false;
        System.out.println(name + " has been repaired and is operational.");
    }

    // Get the name of the energy source
    public String getName() {
        return name;
    }

    // Get the energy production rate
    public double getEnergyProductionRate() {
        return energyProductionRate;
    }

    // Check if the energy source is enabled
    public boolean isEnabled() {
        return isEnabled;
    }

    // Check if the energy source is faulty
    public boolean isFaulty() {
        return isFaulty;
    }
}
