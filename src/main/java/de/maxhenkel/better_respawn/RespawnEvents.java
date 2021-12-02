package de.maxhenkel.better_respawn;

import de.maxhenkel.better_respawn.capabilities.RespawnPosition;
import de.maxhenkel.better_respawn.capabilities.SpawnPointCapabilityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class RespawnEvents {

    private static final int FIND_SPAWN_ATTEMPTS = 16;
    private static final ResourceLocation RESPAWN_CAPABILITY_ID = new ResourceLocation(Main.MODID, "respawn_location");

    private final Random random;

    public RespawnEvents() {
        random = new Random();
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.getLevel().dimensionType().bedWorks()) {
            return;
        }

        BlockPos bedLocation = player.getRespawnPosition();

        if (bedLocation != null) {
            Optional<Vec3> vec3d = Player.findRespawnPositionAndUseSpawnBlock(player.getLevel(), bedLocation, 0F, false, false);
            if (vec3d.isPresent()) {
                Vec3 spawn = vec3d.get();
                if (player.blockPosition().distManhattan(new Vec3i(spawn.x, spawn.y, spawn.z)) <= Main.SERVER_CONFIG.bedRange.get()) {
                    Main.LOGGER.info("Player {} is within the range of its respawn block", player.getName().getContents());
                    return;
                }
            }
        }

        BlockPos respawnPos = findValidRespawnLocation(player.getLevel(), player.blockPosition());

        if (respawnPos == null) {
            return;
        }

        player.setRespawnPosition(player.level.dimension(), respawnPos, 0F, true, false);
        Main.LOGGER.info("Set temporary respawn location to [{}, {}, {}]", respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getEntity().isAlive()) {
            return;
        }
        RespawnPosition respawnPosition = player.getCapability(Main.RESPAWN_CAPABILITY).orElse(null);

        if (respawnPosition == null) {
            Main.LOGGER.error("Player {} has no respawn location capability", player.getName().getContents());
            return;
        }
        BlockPos respawn = respawnPosition.getPos(event.getWorld());
        player.setRespawnPosition(event.getWorld().dimension(), respawn, 0F, false, false);
        if (respawn == null) {
            Main.LOGGER.info("Setting the players respawn position back to world spawn");
        } else {
            Main.LOGGER.info("Setting the players respawn position back to [{}, {}, {}]", respawn.getX(), respawn.getY(), respawn.getZ());
        }
    }

    @SubscribeEvent
    public void onSetSpawn(PlayerSetSpawnEvent event) {
        if (event.isForced()) {
            return;
        }
        BlockPos newSpawn = event.getNewSpawn();
        if (newSpawn != null) {
            event.getPlayer().getCapability(Main.RESPAWN_CAPABILITY).ifPresent(respawnPosition -> respawnPosition.setPos(event.getPlayer().level, newSpawn));
            Main.LOGGER.info("Updating the respawn location of player {} to [{}, {}, {}]", event.getPlayer().getName().getContents(), newSpawn.getX(), newSpawn.getY(), newSpawn.getZ());
        }
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(Main.RESPAWN_CAPABILITY).ifPresent(respawnPosition -> {
            event.getPlayer().getCapability(Main.RESPAWN_CAPABILITY).ifPresent(respawnPosition1 -> {
                respawnPosition1.copyFrom(respawnPosition);
                Main.LOGGER.info("Copying respawn location capability of player {}", event.getPlayer().getName().getContents());
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public void onPlayerCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer)) {
            if (!event.getObject().getCapability(Main.RESPAWN_CAPABILITY).isPresent()) {
                event.addCapability(RESPAWN_CAPABILITY_ID, new SpawnPointCapabilityProvider());
                Main.LOGGER.info("Attaching respawn capability to player");
            }
        }
    }

    @Nullable
    public BlockPos findValidRespawnLocation(ServerLevel world, BlockPos deathLocation) {
        int min = Main.SERVER_CONFIG.minRespawnDistance.get();
        int max = Main.SERVER_CONFIG.maxRespawnDistance.get();

        BlockPos pos = null;
        for (int i = 0; i < FIND_SPAWN_ATTEMPTS && pos == null; i++) {
            Main.LOGGER.info("Searching for respawn location - Attempt {}/{}", i + 1, FIND_SPAWN_ATTEMPTS);
            pos = PlayerRespawnLogic.getSpawnPosInChunk(world, new ChunkPos(new BlockPos(getRandomRange(deathLocation.getX(), min, max), 0, getRandomRange(deathLocation.getZ(), min, max))));
        }
        if (pos == null) {
            Main.LOGGER.info("Found no valid respawn location after {} attempts", FIND_SPAWN_ATTEMPTS);
        } else {
            Main.LOGGER.info("Found valid respawn location: [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
        }
        return pos;
    }

    private int getRandomRange(int actual, int minDistance, int maxDistance) {
        return actual + (random.nextBoolean() ? -1 : 1) * (minDistance + random.nextInt(maxDistance - minDistance));
    }

}