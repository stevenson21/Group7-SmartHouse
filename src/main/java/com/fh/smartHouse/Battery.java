package com.fh.smartHouse;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Battery {
    private final double capacity; // Maximum battery capacity
    private double currentCharge; // Current charge level
    private final Lock lock = new ReentrantLock(); // Lock for thread safety
    private final Semaphore usageSemaphore; // Controls concurrent usage
    private final List<EnergySource> energySources; // List of energy sources for charging

    public Battery(double capacity, int maxUsageSlots, List<EnergySource> energySources) {
        this.capacity = capacity;
        this.currentCharge = 0;
        this.usageSemaphore = new Semaphore(maxUsageSlots); // Limit concurrent usage
        this.energySources = energySources;
    }

    // Start charging from all enabled energy sources
    public void chargeBattery() {
        for (EnergySource source : energySources) {
            if (source.isEnabled()) {
                try {
                    double producedEnergy = source.produceEnergy();
                    lock.lock();
                    try {
                        currentCharge = Math.min(capacity, currentCharge + producedEnergy);
                        System.out.println("Charged " + producedEnergy + " units from " + source.getName()
                                + ". Current charge: " + currentCharge + "/" + capacity);
                        logChargingActivity(source.getName(), producedEnergy);
                    } finally {
                        lock.unlock();
                    }
                } catch (Exception e) {
                    System.err.println("Error charging from " + source.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    // Start using energy
    public void useEnergy(double amount) throws EMSUsageException, InsufficientChargeException {
        if (!usageSemaphore.tryAcquire()) {
            throw new EMSUsageException("Usage limit reached. Try again later.");
        }
        lock.lock();
        try {
            if (currentCharge >= amount) {
                currentCharge -= amount;
                System.out.println("Used " + amount + " energy. Remaining charge: " + currentCharge + "/" + capacity);
            } else {
                throw new InsufficientChargeException("Not enough charge to use " + amount + " energy.");
            }
        } finally {
            lock.unlock();
            usageSemaphore.release();
        }
    }

    // Log charging activity
    private void logChargingActivity(String source, double amount) {
        String homeDirectory = System.getProperty("user.home");
        Path logFile = Paths.get(homeDirectory, "energy_log.txt");
        try {
            Files.write(logFile,
                    (source + " charged " + amount + " units at " + System.currentTimeMillis() + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Charging logged: " + source + ", " + amount + " units.");
        } catch (IOException e) {
            System.err.println("Failed to log charging activity: " + e.getMessage());
        }
    }

    // Get current charge level
    public synchronized double getCurrentCharge() {
        return currentCharge;
    }
}

