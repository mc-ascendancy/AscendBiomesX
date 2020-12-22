package com.ascendancyproject.ascendbiomes;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AscendBiomes extends JavaPlugin {
    private static AscendBiomes instance;

    @Override
    public void onEnable() {
        instance = this;

        // Load JSON config file.
        File configFile = new File(getDataFolder(), Config.location);
        Config.init(configFile);

        // Register events.
        // TODO: register events.

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
