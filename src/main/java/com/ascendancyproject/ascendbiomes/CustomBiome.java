package com.ascendancyproject.ascendbiomes;

import java.util.HashMap;

public class CustomBiome {
    private HashMap<String, Float> cropGrowthRates;

    public void inherit() {
        // TODO: see issue #2.
    }

    public HashMap<String, Float> getCropGrowthRates() {
        return cropGrowthRates;
    }
}
