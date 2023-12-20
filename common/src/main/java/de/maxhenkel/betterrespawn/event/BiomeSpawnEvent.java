package de.maxhenkel.betterrespawn.event;

import de.maxhenkel.betterrespawn.BetterRespawnMod;
import de.maxhenkel.betterrespawn.util.BiomeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ServerLevelData;

import java.util.List;

public class BiomeSpawnEvent {
    public static void onWorldLoad(MinecraftServer server, ServerLevel world) {
        BlockPos spawnPos = null;

        try {
            Registry<Biome> biomeRegistry = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            List<Biome> restrictedBiomes = BiomeUtils.getSpawnBiomeObjects(biomeRegistry);

            logInfo("[Biome Spawn Point] No restricted biomes specified.");

            if (!restrictedBiomes.isEmpty()) {
                logInfo("[Biome Spawn Point] Checking for allowed spawn biomes.");

                String spawnBiome = BiomeUtils.getSpawnBiome();
                if (restrictedBiomes.contains(biomeRegistry.get(new ResourceLocation(spawnBiome)))) {
                    logInfo("[Biome Spawn Point] Spawn biome is allowed.");


                    logInfo("[Biome Spawn Point] The world will now generate.");
                    world.setDefaultSpawnPos(spawnPos, 1.0f);
                } else {
                    logInfo("[Biome Spawn Point] Spawn biome is not allowed. Choosing a default spawn biome.");
                }
            } else {
                logInfo("[Biome Spawn Point] No restricted biomes specified. Choosing a default spawn biome.");
            }

        } catch (Exception ex) {
            logInfo("[Biome Spawn Point] Unable to access Biome Registry on level load.");
        }

        logInfo("[Biome Spawn Point] Unable to find custom spawn point.");
    }

    private static void logInfo(String message) {
        BetterRespawnMod.LOGGER.info(message);
    }
}
