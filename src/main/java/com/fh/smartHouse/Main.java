package com.fh.smartHouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Setting up the log directory and creating necessary objects
        String logDirectory = "logs";
        Metadata metadata = new Metadata();
        LogManager logManager = new LogManager(logDirectory);

        // Initialize energy sources and battery
        List<EnergySource> energySources = new ArrayList<>();
        energySources.add(new EnergySource("Solar Panel", 10.0));
        energySources.add(new EnergySource("Wind Turbine", 15.0));
        energySources.add(new EnergySource("Hydro Generator", 20.0));
        Battery battery = new Battery(100.0, 3, energySources);

        // Initialize smart objects
        List<SmartObject> smartObjects = new ArrayList<>();
        smartObjects.add(new SmartObject("Refrigerator", 2.0));
        smartObjects.add(new SmartObject("Air Conditioning", 5.0));
        smartObjects.add(new SmartObject("Lighting System", 1.0));

        EnergyDataExchange energyDataExchange = new EnergyDataExchange(logDirectory, metadata);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Smart House Management System ---");
            System.out.println(
                    "1. Create Log File\n" +
                    "2. Delete Log File\n" +
                    "3. Move Log File\n" +
                    "4. Archive Old Logs\n" +
                    "5. Log Energy Data\n" +
                    "6. Retrieve Energy Data\n" +
                    "7. Search Log Files\n" +
                    "8. Open Log File\n" +
                    "9. Manage Smart Objects\n" +
                    "10. Manage Energy Sources\n" +
                    "11. View Battery Status\n" +
                    "12. Charge Battery\n" +
                    "13. Simulate Energy Usage\n" +
                    "0. Exit\nChoose an option:");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1: // Create log file
                        System.out.print("Enter log file name: ");
                        String createFileName = scanner.nextLine();
                        logManager.createLogFile(createFileName);
                        break;

                    case 2: // Delete log file
                        System.out.print("Enter log file name to delete: ");
                        String deleteFileName = scanner.nextLine();
                        logManager.deleteLogFile(deleteFileName);
                        break;

                    case 3: // Move log file
                        System.out.print("Enter log file name to move: ");
                        String moveFileName = scanner.nextLine();
                        System.out.print("Enter new directory to move the file: ");
                        String newDirectory = scanner.nextLine();
                        logManager.moveLogFile(moveFileName, newDirectory);
                        break;

                    case 4: // Archive old logs files
                        System.out.print("Enter the number of days to archive logs older than: ");
                        int days = Integer.parseInt(scanner.nextLine());
                        logManager.archiveOldLogs(days);
                        break;

                    case 5: // Log energy data for equipment (e.g., solar or wind stations)
                        System.out.print("Enter equipment name to log energy data for: ");
                        String equipmentName = scanner.nextLine();
                        System.out.print("Enter energy data: ");
                        String energyData = scanner.nextLine();
                        energyDataExchange.logEnergyData(equipmentName, energyData);
                        break;

                    case 6: // Retrieve and display energy data
                        System.out.print("Enter equipment name to retrieve energy data: ");
                        String retrieveEquipment = scanner.nextLine();
                        List<String> data = energyDataExchange.retrieveEnergyData(retrieveEquipment);
                        System.out.println("Energy data for " + retrieveEquipment + ":");
                        data.forEach(System.out::println);
                        break;

                    case 7: // Search log files
                        System.out.print("Enter search criteria (date or keyword): ");
                        String searchCriteria = scanner.nextLine();
                        LogSearcher searcher = new LogSearcher(logDirectory, metadata, searchCriteria);
                        searcher.start();
                        searcher.join(); // Wait for the searcher thread to finish
                        break;

                    case 8: // Open and display a specific log file
                        System.out.print("Enter the log file name to open (e.g., log_2024-10-09.log): ");
                        String openFileName = scanner.nextLine();
                        LogSearcher fileOpener = new LogSearcher(logDirectory, metadata, openFileName);
                        fileOpener.openLogFile(openFileName);  // Open and display log file contents
                        break;

                    case 9: // Manage Smart Objects
                        manageSmartObjects(scanner, smartObjects, logManager);
                        break;

                    case 10: // Manage Energy Sources
                        manageEnergySources(scanner, energySources, logManager);
                        break;

                    case 11: // View Battery Status
                        System.out.println("Battery charge: " + battery.getCurrentCharge() + " units.");
                        break;

                    case 12: // Charge the Battery
                        System.out.println("Starting battery charging...");
                        battery.chargeBattery();
                        break;

                    case 13: // Simulate Energy Usage
                        simulateEnergyUsage(smartObjects, battery, logManager);
                        break;

                    case 0: // Exit
                        running = false;
                        System.out.println("Exiting the system. Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a valid number.");
            } catch (EMSException e) {
                System.err.println("System error: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Operation interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        scanner.close();
    }

    private static void manageSmartObjects(Scanner scanner, List<SmartObject> smartObjects, LogManager logManager) {
        System.out.println("\n--- Manage Smart Objects ---");
        for (int i = 0; i < smartObjects.size(); i++) {
            SmartObject obj = smartObjects.get(i);
            System.out.println((i + 1) + ". " + obj.getName() + " (" + (obj.isOn() ? "ON" : "OFF") + ")");
        }
        System.out.print("Select an object to toggle or repair (0 to go back): ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice > 0 && choice <= smartObjects.size()) {
            SmartObject obj = smartObjects.get(choice - 1);
            System.out.println("1. Toggle ON/OFF\n2. Mark as Faulty\n3. Repair");
            int action = Integer.parseInt(scanner.nextLine());
            switch (action) {
			    case 1:
			        obj.toggleStatus();
			        logManager.logActivity(obj.getName() + " toggled to " + (obj.isOn() ? "ON" : "OFF"));
			        break;
			    case 2:
			        obj.markAsFaulty();
			        logManager.logActivity(obj.getName() + " marked as faulty");
			        break;
			    case 3:
			        obj.repair();
			        logManager.logActivity(obj.getName() + " repaired");
			        break;
			    default:
			        System.out.println("Invalid action.");
			}
        }
    }

    private static void manageEnergySources(Scanner scanner, List<EnergySource> energySources, LogManager logManager) {
        System.out.println("\n--- Manage Energy Sources ---");
        for (int i = 0; i < energySources.size(); i++) {
            EnergySource source = energySources.get(i);
            System.out.println((i + 1) + ". " + source.getName() + " (" + (source.isEnabled() ? "ENABLED" : "DISABLED") + ")");
        }
        System.out.print("Select a source to toggle or repair (0 to go back): ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice > 0 && choice <= energySources.size()) {
            EnergySource source = energySources.get(choice - 1);
            System.out.println("1. Enable/Disable\n2. Mark as Faulty\n3. Repair");
            int action = Integer.parseInt(scanner.nextLine());
            try {
                switch (action) {
                    case 1:
                        source.setEnabled(!source.isEnabled());
                        logManager.logActivity(source.getName() + " set to " + (source.isEnabled() ? "ENABLED" : "DISABLED"));
                        break;
                    case 2:
                        source.markAsFaulty();
                        logManager.logActivity(source.getName() + " marked as faulty");
                        break;
                    case 3:
                        source.repair();
                        logManager.logActivity(source.getName() + " repaired");
                        break;
                    default:
                        System.out.println("Invalid action.");
                }
            } catch (EnergySourceException e) {
                System.err.println("Energy source error: " + e.getMessage());
            }
        }
    }

    private static void simulateEnergyUsage(List<SmartObject> smartObjects, Battery battery, LogManager logManager) throws EMSException {
        double totalUsage = smartObjects.stream()
                .filter(SmartObject::isOn)
                .mapToDouble(SmartObject::getEnergyConsumption)
                .sum();
        try {
            battery.useEnergy(totalUsage);
            logManager.logActivity("Simulated energy usage: " + totalUsage + " units.");
        } catch (InsufficientChargeException e) {
            System.err.println("Energy usage failed: " + e.getMessage());
            logManager.logActivity("Failed energy usage simulation: " + e.getMessage());
        }
    }
}

