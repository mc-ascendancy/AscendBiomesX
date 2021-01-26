package com.ascendancyproject.ascendbiomes.effect;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomEffect {
    private String effect;
    private int amplifier;

    private PotionEffect potionEffect;

    public PotionEffect get() {
        return potionEffect;
    }

    public boolean isValid() {
        return potionEffect != null;
    }

    public void generate() {
        PotionEffectType effectType = PotionEffectType.getByName(effect);
        if (effectType == null) {
            AscendBiomes.getInstance().getLogger().severe("Error loading configuration file; unknown potion effect type: " + effect);
            return;
        }

        potionEffect = new PotionEffect(effectType, Integer.MAX_VALUE, amplifier);
    }
}
