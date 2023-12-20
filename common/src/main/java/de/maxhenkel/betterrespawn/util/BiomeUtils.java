package de.maxhenkel.betterrespawn.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeUtils {
    private static final List<String> spawnBiomes = new ArrayList<>();
    private static final List<Biome> processedBiomes = new ArrayList<>();

    // Add the hardcoded biome names directly to the spawnBiomes list
    static {
        spawnBiomes.add("minecraft:plains");
        spawnBiomes.add("minecraft:forest");
        spawnBiomes.add("minecraft:desert");
        // Add more biomes as needed
    }



    public static int spawnBiomeListSize() {
        return spawnBiomes.size();
    }

    public static List<String> getSpawnBiomes() {
        return new ArrayList<>(spawnBiomes);
    }

    public static String getSpawnBiome() {
        Random random = new Random();
        return getSpawnBiomes().get(random.nextInt(spawnBiomeListSize()));

    }

    // Add this method to get the list of Biome objects
    public static List<Biome> getSpawnBiomeObjects(Registry<Biome> biomeRegistry) {
        List<Biome> biomes = new ArrayList<>();
        for (String biomeName : spawnBiomes) {
            ResourceLocation location = new ResourceLocation(biomeName);
            Biome biome = biomeRegistry.get(location);
            if (biome != null) {
                biomes.add(biome);
            }
        }
        return biomes;
    }

}
