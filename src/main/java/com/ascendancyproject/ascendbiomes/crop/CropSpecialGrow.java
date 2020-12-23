package com.ascendancyproject.ascendbiomes.crop;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;
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

    public CropSpecialGrow(BlockSpreadEvent event) {
        switch (event.getNewState().getType()) {
            case KELP:
            case BAMBOO:
            case VINE:
            case TWISTING_VINES:
            case WEEPING_VINES:
                break;

            default:
                return;
        }

        Block block = event.getSource();

        if (shouldProduce(block))
            block.removeMetadata(CropAgeMetadata.productionAttemptKey, AscendBiomes.getInstance());
        else
            event.setCancelled(true);
    }

    public CropSpecialGrow(StructureGrowEvent event) {
        Block block = event.getLocation().getBlock();

        if (shouldProduce(block))
            block.removeMetadata(CropAgeMetadata.productionAttemptKey, AscendBiomes.getInstance());
        else
            event.setCancelled(true);
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

                    if (shouldProduce(block))
                        block.removeMetadata(CropAgeMetadata.productionAttemptKey, AscendBiomes.getInstance());
                    else
                        event.getBlock().setType(Material.AIR);
                }
            }
        }.runTaskLater(AscendBiomes.getInstance(), 1);
    }

    private void growVertical(BlockGrowEvent event) {
        Block block = event.getBlock().getRelative(BlockFace.DOWN);

        if (shouldProduce(block))
            block.removeMetadata(CropAgeMetadata.productionAttemptKey, AscendBiomes.getInstance());
        else
            event.setCancelled(true);
    }

    private boolean shouldProduce(Block block) {
        int tpa = (int)CropGrow.calculateTicksPerAge(block);

        if (!block.hasMetadata(CropAgeMetadata.productionAttemptKey))
            block.setMetadata(CropAgeMetadata.productionAttemptKey, new CropAgeMetadata());

        CropAgeMetadata cropAgeMetadata = (CropAgeMetadata) block.getMetadata(CropAgeMetadata.productionAttemptKey).get(0);
        int age = cropAgeMetadata.getAndIncrement();
        block.setMetadata(CropAgeMetadata.productionAttemptKey, cropAgeMetadata);

        return age >= tpa;
    }
}
