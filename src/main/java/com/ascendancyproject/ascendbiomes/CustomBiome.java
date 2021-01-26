package com.ascendancyproject.ascendbiomes;

import com.ascendancyproject.ascendbiomes.effect.CustomEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomBiome {
    private boolean initialised;
    private String inherit;

    private HashMap<String, Float> cropGrowthRates;
    private HashMap<Material, Float> cropGrowthRatesType;
    private HashMap<String, Float> mobGrowthRates;
    private HashMap<EntityType, Float> mobGrowthRatesType;
    private ArrayList<CustomEffect> statusEffects;

    public void inherit() {
        if (initialised)
            return;

        initialised = true;

        mobGrowthRatesType = new HashMap<>();
        cropGrowthRatesType = new HashMap<>();

        if (cropGrowthRates != null)
            cropGrowthRates.forEach((k, v) -> {
                Material material = Material.getMaterial(k);
                if (material == null) {
                    AscendBiomes.getInstance().getLogger().severe("Error loading configuration file; unknown crop material: " + k);
                    return;
                }

                cropGrowthRatesType.put(material, v);
            });

        if (mobGrowthRates != null)
            mobGrowthRates.forEach((k, v) ->  {
                try {
                    mobGrowthRatesType.put(EntityType.valueOf(k), v);
                } catch (IllegalArgumentException e) {
                    AscendBiomes.getInstance().getLogger().severe("Error loading configuration file; unknown entity type: " + k);
                }
            });

        if (inherit != null) {
            CustomBiome parent = Config.getInstance().getCustomBiomes().get(inherit);

            if (!parent.initialised)
                parent.inherit();

            parent.cropGrowthRatesType.forEach((k, v) -> cropGrowthRatesType.putIfAbsent(k, v));
            parent.mobGrowthRatesType.forEach((k, v) -> mobGrowthRatesType.putIfAbsent(k, v));

            if (statusEffects == null)
                statusEffects = parent.statusEffects;
        }

        if (statusEffects == null)
            statusEffects = new ArrayList<>();

        statusEffects.forEach(CustomEffect::generate);
    }

    public HashMap<Material, Float> getCropGrowthRatesType() {
        return cropGrowthRatesType;
    }

    public HashMap<EntityType, Float> getMobGrowthRatesType() {
        return mobGrowthRatesType;
    }

    public ArrayList<CustomEffect> getStatusEffects() {
        return statusEffects;
    }
}
