package com.ascendancyproject.ascendbiomes;

import com.ascendancyproject.ascendbiomes.effect.CustomEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomBiome {
    private HashMap<String, Float> cropGrowthRates;
    private HashMap<String, Float> mobGrowthRates;
    private ArrayList<CustomEffect> statusEffects;

    public void inherit() {
        // TODO: see issue #2.

        if (cropGrowthRates == null)
            cropGrowthRates = new HashMap<>();

        if (mobGrowthRates == null)
            mobGrowthRates = new HashMap<>();

        if (statusEffects == null)
            statusEffects = new ArrayList<>();
    }

    public HashMap<String, Float> getCropGrowthRates() {
        return cropGrowthRates;
    }

    public HashMap<String, Float> getMobGrowthRates() {
        return mobGrowthRates;
    }

    public ArrayList<CustomEffect> getStatusEffects() {
        return statusEffects;
    }
}
