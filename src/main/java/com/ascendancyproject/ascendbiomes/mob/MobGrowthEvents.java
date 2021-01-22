package com.ascendancyproject.ascendbiomes.mob;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import com.ascendancyproject.ascendbiomes.Config;
import com.ascendancyproject.ascendbiomes.CustomBiome;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public class MobGrowthEvents {
    private static final float defaultGrowthRate = 100.f;

    private final AscendBiomes plugin;
    private final int mobGrowthTickRate;

    public MobGrowthEvents(AscendBiomes plugin) {
        this.plugin = plugin;
        this.mobGrowthTickRate = Config.getInstance().getMobGrowthTickRate();

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::mobGrowthTicker, mobGrowthTickRate, mobGrowthTickRate);
    }

    private void mobGrowthTicker() {
        plugin.getServer().getWorld("world").getEntities().forEach((Entity entity) -> {
            if (!(entity instanceof Ageable))
                return;

            Ageable ageable = (Ageable) entity;

            if (ageable.isAdult())
                return;

            int ageDiff = (int)((getMobGrowthRate(entity) / 100 - 1) * mobGrowthTickRate);
            ageable.setAge(ageable.getAge() + ageDiff);
        });
    }

    private float getMobGrowthRate(Entity entity) {
        CustomBiome customBiome = Config.getInstance().getCustomBiomes().get(entity.getLocation().getBlock().getBiome().name());
        if (customBiome == null)
            return getDefaultGrowthRate(entity);

        Float growthRate = customBiome.getMobGrowthRatesType().get(entity.getType());
        if (growthRate == null)
            return getDefaultGrowthRate(entity);

        return growthRate;
    }

    private float getDefaultGrowthRate(Entity entity) {
        Float growthRate = Config.getInstance().getDefaultMobGrowthRateType().get(entity.getType());
        return growthRate == null ? defaultGrowthRate : growthRate;
    }
}
