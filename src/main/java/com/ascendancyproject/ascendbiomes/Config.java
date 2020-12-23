package com.ascendancyproject.ascendbiomes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class Config {
    public static final String location = "biomes.json";

    private static Config instance;

    private HashMap<String, CustomBiome> customBiomes;
    private HashMap<String, Float> defaultCropGrowthRate;

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
    }

    public static Config getInstance() {
        return instance;
    }

    public HashMap<String, CustomBiome> getCustomBiomes() {
        return customBiomes;
    }

    public HashMap<String, Float> getDefaultCropGrowthRate() {
        return defaultCropGrowthRate;
    }
}
