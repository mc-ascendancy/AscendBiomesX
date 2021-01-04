package com.ascendancyproject.ascendbiomes.effect;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import com.ascendancyproject.ascendbiomes.Config;
import com.ascendancyproject.ascendbiomes.CustomBiome;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EffectEvents implements Listener {
    private final AscendBiomes plugin;

    public EffectEvents(AscendBiomes plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        updateEffects(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        updateEffects(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Apply effects one tick later, after the player has fully spawned in.
        new BukkitRunnable() {
            @Override
            public void run() {
                addEffects(event.getPlayer(), event.getRespawnLocation().getBlock().getBiome());
            }
        }.runTaskLater(plugin, 1L);
    }

    private void updateEffects(Player player, Location from, Location to) {
        Biome biomeFrom = from.getBlock().getBiome();
        Biome biomeTo = to.getBlock().getBiome();

        // If we haven't moved biomes, return.
        if (biomeFrom == biomeTo)
            return;

        // Remove effects from the previous biome.
        removeEffects(player);

        // Add the new biome's effects.
        addEffects(player, biomeTo);
    }

    private void removeEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects())
            if (effect.getType() != PotionEffectType.BAD_OMEN && effect.getDuration() > 1000000)
                player.removePotionEffect(effect.getType());
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
        removeEffects(event.getPlayer());

        addEffects(event.getPlayer(), event.getPlayer().getLocation().getBlock().getBiome());
    }
}
