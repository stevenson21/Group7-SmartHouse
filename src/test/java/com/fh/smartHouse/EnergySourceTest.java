package com.fh.smartHouse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnergySourceTest {

    @Test
    void testEnergyProduction() throws Exception { // Add `throws Exception`
        EnergySource source = new EnergySource("Solar Panel", 15.0);

        double producedEnergy = source.produceEnergy();
        assertEquals(15.0, producedEnergy, "Produced energy should match the configured value.");
    }

    @Test
    void testEnableDisableSource() throws Exception { // Add `throws Exception`
        EnergySource source = new EnergySource("Wind Turbine", 20.0);

        source.setEnabled(false);
        assertFalse(source.isEnabled(), "Energy source should be disabled.");

        source.setEnabled(true);
        assertTrue(source.isEnabled(), "Energy source should be enabled.");
    }

    @Test
    void testFaultyEnergySource() throws Exception { // Add `throws Exception`
        EnergySource source = new EnergySource("Hydro Generator", 30.0);

        // Mark the source as faulty
        source.markAsFaulty();
        assertTrue(source.isFaulty(), "Energy source should be marked as faulty.");

        // Attempt to produce energy and expect an exception
        Exception exception = assertThrows(EnergySourceException.class, source::produceEnergy);
        assertEquals("Hydro Generator is faulty and cannot produce energy.", exception.getMessage(),
                "Exception message should match the expected fault message.");

        // Repair the energy source and ensure it's no longer faulty
        source.repair();
        assertFalse(source.isFaulty(), "Energy source should be repaired and no longer faulty.");
    }
}
