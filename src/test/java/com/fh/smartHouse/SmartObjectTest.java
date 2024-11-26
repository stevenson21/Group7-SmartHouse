package com.fh.smartHouse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SmartObjectTest {

    @Test
    void testToggleStatus() {
        SmartObject obj = new SmartObject("Test Object", 5.0);

        obj.toggleStatus(); // Turn ON
        assertTrue(obj.isOn());
        obj.toggleStatus(); // Turn OFF
        assertFalse(obj.isOn());
    }

    @Test
    void testFaultySmartObject() {
        SmartObject obj = new SmartObject("Faulty Object", 5.0);
        obj.markAsFaulty();

        assertTrue(obj.isFaulty());
        assertEquals(0.0, obj.getEnergyConsumption(), "Faulty object should not consume energy");

        obj.repair();
        assertFalse(obj.isFaulty());
    }

    @Test
    void testEnergyConsumption() {
        SmartObject obj = new SmartObject("Consuming Object", 10.0);

        obj.toggleStatus(); // Turn ON
        assertEquals(10.0, obj.getEnergyConsumption());

        obj.toggleStatus(); // Turn OFF
        assertEquals(0.0, obj.getEnergyConsumption());
    }
}
