package com.ascendancyproject.ascendbiomes.crop;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.BlockGrowEvent;

public class CropGrow {
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

        // TODO: calculate age based on current biome's CustomBiome.
        ageable.setAge(age / 8);

        if (ageable.getAge() == ageable.getMaximumAge())
            event.getBlock().removeMetadata(CropAgeMetadata.key, AscendBiomes.getInstance());

        event.getBlock().setBlockData(ageable);
    }

    private void guessAge(BlockGrowEvent event, Ageable ageable) {
        if (ageable.getAge() == 0) {
            event.getBlock().setMetadata(CropAgeMetadata.key, new CropAgeMetadata());
            return;
        }

        // TODO: guess age from current biome's CustomBiome.
        int age = ageable.getAge() / 64;

        event.getBlock().setMetadata(CropAgeMetadata.key, new CropAgeMetadata(age));
    }
}
