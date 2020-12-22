package com.ascendancyproject.ascendbiomes.crop;

import com.ascendancyproject.ascendbiomes.AscendBiomes;
import org.bukkit.metadata.MetadataValueAdapter;

public class CropAgeMetadata extends MetadataValueAdapter {
    public static final String key = "ascendbiomes-age";

    int age;

    public CropAgeMetadata() {
        super(AscendBiomes.getInstance());
        age = 0;
    }

    public CropAgeMetadata(int age) {
        super(AscendBiomes.getInstance());
        this.age = age;
    }

    public int getAndIncrement() {
        return age++;
    }

    @Override
    public Object value() {
        return age;
    }

    @Override
    public void invalidate() {
        age = 0;
    }
}
