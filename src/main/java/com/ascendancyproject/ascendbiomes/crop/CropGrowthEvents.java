package com.ascendancyproject.ascendbiomes.crop;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class CropGrowthEvents implements Listener {
    AscendBiomes plugin;

    public CropGrowthEvents(AscendBiomes plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        // If the block is a normally growing plant, such as wheat, do the normal checks.
        // If this is not the case, it is a special growth event, such as sugar cane, or melons.
        if (event.getBlock().getType() != Material.AIR)
            new CropGrow(event);
        else
            new CropSpecialGrow(event);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        new CropSpecialGrow(event);
    }

    @EventHandler
    public void onBlockSpread(StructureGrowEvent event) {
        new CropSpecialGrow(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Remove crop metadata on blocks that have it when they are broken.
        if (event.getBlock().hasMetadata(CropAgeMetadata.key))
            event.getBlock().removeMetadata(CropAgeMetadata.key, AscendBiomes.getInstance());

        if (event.getBlock().hasMetadata(CropAgeMetadata.productionAttemptKey))
            event.getBlock().removeMetadata(CropAgeMetadata.productionAttemptKey, AscendBiomes.getInstance());
    }
}
