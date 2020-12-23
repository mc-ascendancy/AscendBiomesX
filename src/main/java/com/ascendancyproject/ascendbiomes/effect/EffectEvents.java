package com.ascendancyproject.ascendbiomes.effect;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import com.ascendancyproject.ascendbiomes.Config;
import com.ascendancyproject.ascendbiomes.CustomBiome;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectEvents implements Listener {
    private final AscendBiomes plugin;

    public EffectEvents(AscendBiomes plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Biome biomeFrom = event.getFrom().getBlock().getBiome();
        Biome biomeTo = event.getTo().getBlock().getBiome();

        // If we haven't moved biomes, return.
        if (biomeFrom == biomeTo)
            return;

        // Remove effects from the previous biome.
        removeEffects(event.getPlayer(), biomeFrom);

        // Add the new biome's effects.
        addEffects(event.getPlayer(), biomeTo);
    }

    private void removeEffects(Player player, Biome biome) {
        CustomBiome customBiome = Config.getInstance().getCustomBiomes().get(biome.name());
        if (customBiome == null)
            return;

        for (CustomEffect effect : customBiome.getStatusEffects()) {
            if (player.hasPotionEffect(effect.get().getType()))
                player.removePotionEffect(effect.get().getType());
        }
    }

    private void addEffects(Player player, Biome biome) {
        CustomBiome customBiome = Config.getInstance().getCustomBiomes().get(biome.name());
        if (customBiome == null)
            return;

        for (CustomEffect effect : customBiome.getStatusEffects())
            player.addPotionEffect(effect.get());
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (event.getCause() == EntityPotionEffectEvent.Cause.PLUGIN || event.getNewEffect() != null || event.getOldEffect() == null)
            return;

        CustomBiome customBiome = Config.getInstance().getCustomBiomes().get(event.getEntity().getLocation().getBlock().getBiome().name());
        if (customBiome == null)
            return;

        for (CustomEffect effect : customBiome.getStatusEffects()) {
            if (effect.get().getType() == event.getOldEffect().getType() && effect.get().getAmplifier() == event.getOldEffect().getAmplifier()) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Prevent exploit where players disconnect on the edge of a biome to keep status effects.
        for (PotionEffect effect : event.getPlayer().getActivePotionEffects())
            if (effect.getType() != PotionEffectType.BAD_OMEN && effect.getDuration() > 1000000)
                event.getPlayer().removePotionEffect(effect.getType());

        addEffects(event.getPlayer(), event.getPlayer().getLocation().getBlock().getBiome());
    }
}
