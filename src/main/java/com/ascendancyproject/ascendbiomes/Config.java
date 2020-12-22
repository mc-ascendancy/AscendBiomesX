package com.ascendancyproject.ascendbiomes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class Config {
    public static final String location = "biomes.json";

    private static HashMap<String, CustomBiome> customBiomes;

    public static void init(File config) {
        // Write the default config, if none exists.
        if (config.exists()) {
            AscendBiomes.getInstance().getLogger().info("Loaded " + location);
        } else {
            AscendBiomes.getInstance().getLogger().warning("Could not find " + location + "; writing the default config");
            AscendBiomes.getInstance().saveResource("biomes.json", false);
        }

        // Instantiate a default GSON instance, this could be configured here for custom types if necessary.
        Gson gson = new Gson();

        try {
            // Parse the config file using the new GSON instance.
            customBiomes = gson.fromJson(new FileReader(config), new TypeToken<HashMap<String, CustomBiome>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, CustomBiome> getCustomBiomes() {
        return customBiomes;
    }
}
