package com.ascendancyproject.ascendbiomes.crop;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import com.ascendancyproject.ascendbiomes.Config;
import com.ascendancyproject.ascendbiomes.CustomBiome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.BlockGrowEvent;

public class CropGrow {
    private static final float defaultGrowthRate = 100.f;
    private static final float spigotGrowthModifier = 8.f;

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
        ageable.setAge((int)((float)age / tpa));

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
        CustomBiome customBiome = Config.getCustomBiomes().get(block.getBiome().name());
        if (customBiome == null)
            return ticksFromGrowthRate(defaultGrowthRate);

        Float growthRate = customBiome.getCropGrowthRates().get(block.getType().name());
        if (growthRate == null)
            return ticksFromGrowthRate(defaultGrowthRate);

        return ticksFromGrowthRate(growthRate);
    }

    private static float ticksFromGrowthRate(float growthRate) {
        return 1 / (growthRate / 100) * spigotGrowthModifier;
    }
}
