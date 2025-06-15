package de.maxhenkel.betterrespawn;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class RespawnManager {

    private static final int FIND_SPAWN_ATTEMPTS = 16;

    private final Random random;

    public RespawnManager() {
        random = new Random();
    }

    public void onPlayerDeath(ServerPlayer player) {
        if (!(player instanceof RespawnAbilities respawnAbilities)) {
            return;
        }

        @Nullable ServerPlayer.RespawnConfig respawnConfig = player.getRespawnConfig();
        respawnAbilities.better_respawn$setRespawnConfig(respawnConfig);

        if (respawnConfig != null) {
            TeleportTransition transition = player.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);
            if (!transition.missingRespawnBlock()) {
                Vec3 spawn = transition.position();
                if (respawnConfig.dimension() == player.level().dimension() && player.blockPosition().distManhattan(new Vec3i((int) spawn.x, (int) spawn.y, (int) spawn.z)) <= BetterRespawnMod.SERVER_CONFIG.respawnBlockRange.get()) {
                    BetterRespawnMod.LOGGER.info("Player {} is within the range of its respawn block", player.getName().getString());
                    return;
                }
            }
        }

        if (player.level().dimensionType().hasCeiling() || !player.level().dimensionType().bedWorks()) {
            BetterRespawnMod.LOGGER.info("Can't respawn {} in {}", player.getName().getString(), player.level().dimension().location());
            return;
        }

        BlockPos respawnPos = findValidRespawnLocation(player.level(), player.blockPosition());

        if (respawnPos == null) {
            return;
        }

        player.setRespawnPosition(new ServerPlayer.RespawnConfig(player.level().dimension(), respawnPos, 0, true), false);
        BetterRespawnMod.LOGGER.info("Set temporary respawn location to [{}, {}, {}]", respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
    }

    public void onSetRespawnPosition(ServerPlayer player, @Nullable ServerPlayer.RespawnConfig respawnConfig, boolean showMessage) {
        if (respawnConfig != null && respawnConfig.forced()) {
            return;
        }

        if (!(player instanceof RespawnAbilities abilities)) {
            return;
        }

        abilities.better_respawn$setRespawnConfig(respawnConfig);

        if (respawnConfig != null) {
            BetterRespawnMod.LOGGER.info("Updating the respawn location of player {} to [{}, {}, {}] in {}", player.getName().getString(), respawnConfig.pos().getX(), respawnConfig.pos().getY(), respawnConfig.pos().getZ(), respawnConfig.dimension().location());
        } else {
            BetterRespawnMod.LOGGER.info("Updating the respawn location of player {} to [NONE]", player.getName().getString());
        }
    }

    @Nullable
    public BlockPos findValidRespawnLocation(ServerLevel world, BlockPos deathLocation) {
        int min = BetterRespawnMod.SERVER_CONFIG.minRespawnDistance.get();
        int max = BetterRespawnMod.SERVER_CONFIG.maxRespawnDistance.get();

        BlockPos pos = null;
        for (int i = 0; i < FIND_SPAWN_ATTEMPTS && pos == null; i++) {
            BetterRespawnMod.LOGGER.info("Searching for respawn location - Attempt {}/{}", i + 1, FIND_SPAWN_ATTEMPTS);
            pos = PlayerRespawnLogic.getSpawnPosInChunk(world, new ChunkPos(new BlockPos(getRandomRange(deathLocation.getX(), min, max), 0, getRandomRange(deathLocation.getZ(), min, max))));
            if (pos != null && !world.getWorldBorder().isWithinBounds(pos)) {
                pos = null;
            }
        }
        if (pos == null) {
            BetterRespawnMod.LOGGER.info("Found no valid respawn location after {} attempts", FIND_SPAWN_ATTEMPTS);
        } else {
            BetterRespawnMod.LOGGER.info("Found valid respawn location: [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
        }
        return pos;
    }

    private int getRandomRange(int actual, int minDistance, int maxDistance) {
        return actual + (random.nextBoolean() ? -1 : 1) * (minDistance + random.nextInt(maxDistance - minDistance));
    }

}
