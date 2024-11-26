package com.fh.smartHouse;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BatteryTest {

    @Test
    void testChargeBattery() {
        EnergySource solar = new EnergySource("Solar Panel", 10.0);
        EnergySource wind = new EnergySource("Wind Turbine", 15.0);
        Battery battery = new Battery(100.0, 2, List.of(solar, wind));

        battery.chargeBattery();
        assertEquals(25.0, battery.getCurrentCharge(), 0.01);
    }

    @Test
    void testUseEnergy() throws EMSException {
        Battery battery = new Battery(50.0, 1, List.of());
        battery.chargeBattery(); // Default charge is 0, no sources

        Exception exception = assertThrows(InsufficientChargeException.class, () -> battery.useEnergy(10.0));
        assertEquals("Not enough charge to use 10.0 energy.", exception.getMessage());
    }

    @Test
    void testConcurrentUsage() throws EMSException {
        Battery battery = new Battery(100.0, 1, List.of());
        battery.chargeBattery();

        battery.useEnergy(50.0); // Valid usage
        assertEquals(50.0, battery.getCurrentCharge());

        Exception exception = assertThrows(InsufficientChargeException.class, () -> battery.useEnergy(60.0));
        assertEquals("Not enough charge to use 60.0 energy.", exception.getMessage());
    }
}

