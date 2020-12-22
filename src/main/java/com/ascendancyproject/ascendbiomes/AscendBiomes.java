package com.ascendancyproject.ascendbiomes;

import com.ascendancyproject.ascendbiomes.crop.CropGrowthEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AscendBiomes extends JavaPlugin {
    private static AscendBiomes instance;

    @Override
    public void onEnable() {
        instance = this;

        // Load JSON config file.
        File configFile = new File(getDataFolder(), Config.location);
        Config.init(configFile, this);

        // Register events.
        new CropGrowthEvents(this);
        getLogger().info("Registered events");

        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled");
    }

    public static AscendBiomes getInstance() {
        return instance;
    }
}
