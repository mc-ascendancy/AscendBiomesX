package com.ascendancyproject.ascendbiomes.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomEffect {
    private String effect;
    private int amplifier;

    private PotionEffect potionEffect;

    public PotionEffect get() {
        if (potionEffect == null)
            generate();

        return potionEffect;
    }

    private void generate() {
        potionEffect = new PotionEffect(PotionEffectType.getByName(effect), Integer.MAX_VALUE, amplifier);
    }
}