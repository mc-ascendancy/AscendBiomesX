package com.ascendancyproject.ascendbiomes.crop;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import com.ascendancyproject.ascendbiomes.Config;
import com.ascendancyproject.ascendbiomes.CustomBiome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.BlockGrowEvent;

public class CropGrow {
    private static final float defaultGrowthRate = 100.f;
    public static final float spigotGrowthModifier = 8.f;

    public CropGrow(BlockGrowEvent event) {
        if (!(event.getBlock().getBlockData() instanceof Ageable))
            return;

        event.setCancelled(true);

        Ageable ageable = (Ageable) event.getBlock().getBlockData();

        if (!event.getBlock().hasMetadata(CropAgeMetadata.key))
            guessAge(event, ageable);

        CropAgeMetadata metadata = (CropAgeMetadata) event.getBlock().getMetadata(CropAgeMetadata.key).get(0);
        int age = metadata.getAndIncrement();
        event.getBlock().setMetadata(CropAgeMetadata.key, metadata);

        float tpa = calculateTicksPerAge(event.getBlock());
        ageable.setAge(Math.min((int)((float)age / tpa), ageable.getMaximumAge()));

        if (ageable.getAge() == ageable.getMaximumAge())
            event.getBlock().removeMetadata(CropAgeMetadata.key, AscendBiomes.getInstance());

        event.getBlock().setBlockData(ageable);
    }

    private void guessAge(BlockGrowEvent event, Ageable ageable) {
        if (ageable.getAge() == 0) {
            event.getBlock().setMetadata(CropAgeMetadata.key, new CropAgeMetadata());
            return;
        }

        int age = (int)((float)ageable.getAge() * calculateTicksPerAge(event.getBlock()));

        event.getBlock().setMetadata(CropAgeMetadata.key, new CropAgeMetadata(age));
    }

    public static float calculateTicksPerAge(Block block) {
        CustomBiome customBiome = Config.getInstance().getCustomBiomes().get(block.getBiome().name());
        if (customBiome == null)
            return ticksFromGrowthRate(getDefaultGrowthRate(block));

        Float growthRate = customBiome.getCropGrowthRatesType().get(block.getType());
        if (growthRate == null)
            return ticksFromGrowthRate(getDefaultGrowthRate(block));

        return ticksFromGrowthRate(growthRate);
    }

    private static float getDefaultGrowthRate(Block block) {
        Float growthRate = Config.getInstance().getDefaultCropGrowthRateType().get(block.getType());
        return growthRate == null ? defaultGrowthRate : growthRate;
    }

    private static float ticksFromGrowthRate(float growthRate) {
        return 1 / (growthRate / 100) * spigotGrowthModifier;
    }
}
