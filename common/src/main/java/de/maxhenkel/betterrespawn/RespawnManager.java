package de.maxhenkel.betterrespawn;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

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

        if (respawnAbilities.better_respawn$getRespawnSearch() != null) {
            // The player died again without respawning in between - the cached config would be the temporary one
            return;
        }

        @Nullable ServerPlayer.RespawnConfig respawnConfig = player.getRespawnConfig();
        respawnAbilities.better_respawn$setRespawnConfig(respawnConfig);

        if (respawnConfig != null) {
            TeleportTransition transition = player.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);
            if (!transition.missingRespawnBlock()) {
                Vec3 spawn = transition.position();
                if (respawnConfig.respawnData().dimension() == player.level().dimension() && player.blockPosition().distManhattan(new Vec3i((int) spawn.x, (int) spawn.y, (int) spawn.z)) <= BetterRespawnMod.SERVER_CONFIG.respawnBlockRange.get()) {
                    BetterRespawnMod.LOGGER.info("Player {} is within the range of its respawn block", player.getName().getString());
                    return;
                }
            }
        }

        respawnAbilities.better_respawn$setRespawnSearch(searchRespawnLocation(player, player.level(), 1).exceptionally(throwable -> {
            BetterRespawnMod.LOGGER.error("Failed to find a respawn location for player {}", player.getName().getString(), throwable);
            return null;
        }));
    }

    public void awaitRespawnSearch(ServerPlayer player) {
        if (!(player instanceof RespawnAbilities respawnAbilities)) {
            return;
        }

        CompletableFuture<?> search = respawnAbilities.better_respawn$getRespawnSearch();
        if (search == null) {
            return;
        }

        respawnAbilities.better_respawn$setRespawnSearch(null);
        // The search usually finishes while the player is still on the death screen, so this rarely has to wait
        player.level().getServer().managedBlock(search::isDone);
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
            BetterRespawnMod.LOGGER.info("Updating the respawn location of player {} to [{}, {}, {}] in {}", player.getName().getString(), respawnConfig.respawnData().pos().getX(), respawnConfig.respawnData().pos().getY(), respawnConfig.respawnData().pos().getZ(), respawnConfig.respawnData().dimension().identifier());
        } else {
            BetterRespawnMod.LOGGER.info("Updating the respawn location of player {} to [NONE]", player.getName().getString());
        }
    }

    private CompletableFuture<Void> searchRespawnLocation(ServerPlayer player, ServerLevel level, int attempt) {
        BlockPos searchOrigin = getRandomSearchOrigin(level, player.blockPosition());
        BetterRespawnMod.LOGGER.info("Searching for a respawn location around [{}, {}, {}] - Attempt {}/{}", searchOrigin.getX(), searchOrigin.getY(), searchOrigin.getZ(), attempt, FIND_SPAWN_ATTEMPTS);
        return PlayerSpawnFinder.findSpawn(level, searchOrigin).thenCompose(respawnPos -> {
            if (player.isRemoved()) {
                // The player disconnected or respawned before the search finished
                return CompletableFuture.completedFuture(null);
            }
            BlockPos pos = BlockPos.containing(respawnPos);
            if (isValidRespawnLocation(level, pos)) {
                setTemporaryRespawnPosition(player, pos);
                return CompletableFuture.completedFuture(null);
            }
            if (attempt >= FIND_SPAWN_ATTEMPTS) {
                BetterRespawnMod.LOGGER.info("Found no valid respawn location after {} attempts", FIND_SPAWN_ATTEMPTS);
                return CompletableFuture.completedFuture(null);
            }
            return searchRespawnLocation(player, level, attempt + 1);
        });
    }

    private boolean isValidRespawnLocation(ServerLevel level, BlockPos pos) {
        if (pos.getY() >= level.getMinY() + level.dimensionType().logicalHeight()) {
            return false;
        }
        BlockPos below = pos.below();
        return Block.isFaceFull(level.getBlockState(below).getCollisionShape(level, below), Direction.UP);
    }

    private void setTemporaryRespawnPosition(ServerPlayer player, BlockPos pos) {
        player.setRespawnPosition(new ServerPlayer.RespawnConfig(new LevelData.RespawnData(new GlobalPos(player.level().dimension(), pos), 0F, 0F), true), false);
        BetterRespawnMod.LOGGER.info("Set temporary respawn location to [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
    }

    private BlockPos getRandomSearchOrigin(ServerLevel level, BlockPos deathLocation) {
        int min = BetterRespawnMod.SERVER_CONFIG.minRespawnDistance.get();
        int max = Math.max(BetterRespawnMod.SERVER_CONFIG.maxRespawnDistance.get(), min);
        return level.getWorldBorder().clampToBounds(getRandomRange(deathLocation.getX(), min, max), deathLocation.getY(), getRandomRange(deathLocation.getZ(), min, max));
    }

    private int getRandomRange(int actual, int minDistance, int maxDistance) {
        return actual + (random.nextBoolean() ? -1 : 1) * random.nextInt(minDistance, maxDistance + 1);
    }

}
