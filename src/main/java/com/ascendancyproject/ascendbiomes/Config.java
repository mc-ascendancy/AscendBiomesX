package com.ascendancyproject.ascendbiomes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static final String location = "biomes.json";

    private static Config instance;

    private HashMap<String, CustomBiome> customBiomes;
    private HashMap<String, Float> defaultCropGrowthRate;
    private HashMap<Material, Float> defaultCropGrowthRateType;
    private int mobGrowthTickRate;
    private HashMap<String, Float> defaultMobGrowthRate;
    private HashMap<EntityType, Float> defaultMobGrowthRateType;

    public static void init(File config, AscendBiomes plugin) {
        // Write the default config, if none exists.
        if (config.exists()) {
            plugin.getLogger().info("Loaded " + location);
        } else {
            plugin.getLogger().warning("Could not find " + location + "; writing the default config");
            plugin.saveResource("biomes.json", false);
        }

        // Instantiate a default GSON instance, this could be configured here for custom types if necessary.
        Gson gson = new Gson();

        try {
            // Parse the config file using the new GSON instance.
            instance = gson.fromJson(new FileReader(config), new TypeToken<Config>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        instance.defaultCropGrowthRateType = new HashMap<>();
        instance.defaultMobGrowthRateType = new HashMap<>();

        instance.defaultCropGrowthRate.forEach((k, v) -> instance.defaultCropGrowthRateType.put(Material.getMaterial(k), v));
        instance.defaultMobGrowthRate.forEach((k, v) -> instance.defaultMobGrowthRateType.put(EntityType.valueOf(k), v));

        for (Map.Entry<String, CustomBiome> cursor : instance.getCustomBiomes().entrySet())
            cursor.getValue().inherit();
    }

    public static Config getInstance() {
        return instance;
    }

    public HashMap<String, CustomBiome> getCustomBiomes() {
        return customBiomes;
    }

    public HashMap<Material, Float> getDefaultCropGrowthRateType() {
        return defaultCropGrowthRateType;
    }

    public int getMobGrowthTickRate() {
        return mobGrowthTickRate;
    }

    public HashMap<EntityType, Float> getDefaultMobGrowthRateType() {
        return defaultMobGrowthRateType;
    }
}
