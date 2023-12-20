package de.maxhenkel.betterrespawn.config;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class RespawnLocationConfig {

    private static boolean witherKilled = false;


    public static int getMinDistance() {
        return 1000;
    }

    public static int getMaxDistance(ServerLevel world) {
        int max = 2500;

        // Check server conditions and update max distance accordingly
        if (hasAccessedNether(world)) {
            max = 3000;
        }

        if (hasKilledWither(world)) {
            max = 40000;
        }

        if (hasAccessedEnd(world)) {
            max = 5000;
        }

        return max;
    }

    private void onWorldLoad(MinecraftServer server, ServerLevel world) {
        // Check if the Wither has been killed when the world loads
        if (!witherKilled) {
            witherKilled = hasKilledWither(world);
        }
    }
    private static boolean hasAccessedNether(ServerLevel world) {
        // Implement your logic to check if Nether has been accessed
        // Example: Check if the server has a Nether portal
        return world.dimensionType().hasCeiling();
    }




    private static boolean hasKilledWither(ServerLevel world) {
        // Check if the Wither boss entity is dead
        for (WitherBoss witherBoss : world.getEntities(EntityType.WITHER, w -> true)) {
            return witherBoss.isDeadOrDying();
        }
        return false;
    }

    private static boolean hasAccessedEnd(ServerLevel world) {
        // Implement your logic to check if End has been accessed
        // Example: Check if the server has an End portal
        return world.dimension().equals(Level.END);
    }


}