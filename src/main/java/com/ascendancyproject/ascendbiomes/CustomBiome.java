package com.ascendancyproject.ascendbiomes;

import com.ascendancyproject.ascendbiomes.effect.CustomEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomBiome {
    private boolean initialised;
    private String inherit;

    private HashMap<String, Float> cropGrowthRates;
    private HashMap<String, Float> mobGrowthRates;
    private ArrayList<CustomEffect> statusEffects;

    public void inherit() {
        if (initialised)
            return;

        initialised = true;

        if (cropGrowthRates == null)
            cropGrowthRates = new HashMap<>();

        if (mobGrowthRates == null)
            mobGrowthRates = new HashMap<>();

        if (inherit != null) {
            CustomBiome parent = Config.getInstance().getCustomBiomes().get(inherit);

            if (!parent.initialised)
                parent.inherit();

            parent.cropGrowthRates.forEach((k, v) -> cropGrowthRates.putIfAbsent(k, v));
            parent.mobGrowthRates.forEach((k, v) -> cropGrowthRates.putIfAbsent(k, v));

            if (statusEffects == null)
                statusEffects = parent.statusEffects;
        }

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
