package com.ascendancyproject.ascendbiomes.crop;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CropSpecialGrow {
    private static final BlockFace []directions = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    public CropSpecialGrow(BlockGrowEvent event) {
        switch (event.getNewState().getType()) {
            case MELON:
            case PUMPKIN:
                growStem(event);
                break;

            case CACTUS:
            case SUGAR_CANE:
                growVertical(event);
                break;
        }
    }

    private void growStem(BlockGrowEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (BlockFace direction : directions) {
                    Block block = event.getBlock().getRelative(direction);
                    if (!(block.getBlockData() instanceof Directional))
                        continue;

                    Directional directional = (Directional) block.getBlockData();
                    if (direction != directional.getFacing().getOppositeFace())
                        continue;

                    int tpa = (int)CropGrow.calculateTicksPerAge(block);

                    if (!block.hasMetadata(CropAgeMetadata.productionAttemptKey))
                        block.setMetadata(CropAgeMetadata.productionAttemptKey, new CropAgeMetadata());

                    CropAgeMetadata cropAgeMetadata = (CropAgeMetadata) block.getMetadata(CropAgeMetadata.productionAttemptKey).get(0);
                    int age = cropAgeMetadata.getAndIncrement();
                    block.setMetadata(CropAgeMetadata.productionAttemptKey, cropAgeMetadata);

                    if (age < tpa)
                        event.getBlock().setType(Material.AIR);
                    else
                        block.removeMetadata(CropAgeMetadata.productionAttemptKey, AscendBiomes.getInstance());
                }
            }
        }.runTaskLater(AscendBiomes.getInstance(), 1);
    }

    private void growVertical(BlockGrowEvent event) {

    }
}
