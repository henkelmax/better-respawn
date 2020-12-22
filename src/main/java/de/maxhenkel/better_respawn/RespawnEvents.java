package de.maxhenkel.better_respawn;

import de.maxhenkel.better_respawn.capabilities.RespawnPosition;
import de.maxhenkel.better_respawn.capabilities.SpawnPointCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.SpawnLocationHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
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

    private Random random;


    public RespawnEvents() {
        random = new Random();
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

        // canRespawnHere
        if (!player.getServerWorld().getDimensionType().doesBedWork()) {
            return;
        }

        BlockPos bedLocation = player.func_241140_K_();

        if (bedLocation != null) {
            Optional<Vector3d> vec3d = PlayerEntity.func_242374_a(player.getServerWorld(), bedLocation, 0F, false, false);
            if (vec3d.isPresent()) {
                Vector3d spawn = vec3d.get();
                if (player.getPosition().manhattanDistance(new Vector3i(spawn.x, spawn.y, spawn.z)) <= Main.SERVER_CONFIG.bedRange.get()) {
                    Main.LOGGER.debug("Player {} is within the range of its bed", player.getName().getUnformattedComponentText());
                    return;
                }
            }
        }

        BlockPos respawnPos = findValidRespawnLocation(player.getServerWorld(), player.getPosition());

        if (respawnPos == null) {
            return;
        }

        player.func_242111_a(player.world.getDimensionKey(), respawnPos, 0F, true, false);
        Main.LOGGER.debug("Set temporary respawn location to [{}, {}, {}]", respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof ServerPlayerEntity) || !event.getEntity().isAlive()) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
        RespawnPosition respawnPosition = player.getCapability(Main.RESPAWN_CAPABILITY).orElse(null);

        if (respawnPosition == null) {
            player.func_242111_a(event.getWorld().getDimensionKey(), null, 0F, false, false);
            Main.LOGGER.error("Player {} has no respawn location capability", player.getName().getUnformattedComponentText());
            return;
        }
        BlockPos respawn = respawnPosition.getPos(event.getWorld());
        player.func_242111_a(event.getWorld().getDimensionKey(), respawn, 0F, false, false);
        if (respawn == null) {
            Main.LOGGER.debug("Setting the players respawn position back to world spawn");
        } else {
            Main.LOGGER.debug("Setting the players respawn position back to [{}, {}, {}]", respawn.getX(), respawn.getY(), respawn.getZ());
        }
    }

    @SubscribeEvent
    public void onSetSpawn(PlayerSetSpawnEvent event) {
        if (event.isForced()) {
            return;
        }
        BlockPos newSpawn = event.getNewSpawn();
        if (newSpawn != null) {
            event.getPlayer().getCapability(Main.RESPAWN_CAPABILITY).ifPresent(respawnPosition -> respawnPosition.setPos(event.getPlayer().world, newSpawn));
            Main.LOGGER.debug("Updating the respawn location of player {} to [{}, {}, {}]", event.getPlayer().getName().getUnformattedComponentText(), newSpawn.getX(), newSpawn.getY(), newSpawn.getZ());
        }
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        event.getOriginal().getCapability(Main.RESPAWN_CAPABILITY).ifPresent(respawnPosition -> {
            event.getPlayer().getCapability(Main.RESPAWN_CAPABILITY).ifPresent(respawnPosition1 -> {
                respawnPosition1.copyFrom(respawnPosition);
                Main.LOGGER.debug("Copying respawn location capability of player {}", event.getPlayer().getName().getUnformattedComponentText());
            });
        });
    }

    @SubscribeEvent
    public void onPlayerCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity && !(event.getObject() instanceof FakePlayer)) {
            if (!event.getObject().getCapability(Main.RESPAWN_CAPABILITY).isPresent()) {
                event.addCapability(RESPAWN_CAPABILITY_ID, new SpawnPointCapabilityProvider());
                Main.LOGGER.debug("Attaching respawn capability to player");
            }
        }
    }

    @Nullable
    public BlockPos findValidRespawnLocation(ServerWorld world, BlockPos deathLocation) {
        int min = Main.SERVER_CONFIG.minRespawnDistance.get();
        int max = Main.SERVER_CONFIG.maxRespawnDistance.get();

        BlockPos pos = null;
        for (int i = 0; i < FIND_SPAWN_ATTEMPTS && pos == null; i++) {
            Main.LOGGER.debug("Searching for respawn location - Attempt {}/{}", i + 1, FIND_SPAWN_ATTEMPTS);
            pos = SpawnLocationHelper.func_241094_a_(world, new ChunkPos(new BlockPos(getRandomRange(deathLocation.getX(), min, max), 0, getRandomRange(deathLocation.getZ(), min, max))), true);
        }
        if (pos == null) {
            Main.LOGGER.debug("Found no valid respawn location after {} attempts", FIND_SPAWN_ATTEMPTS);
        } else {
            Main.LOGGER.debug("Found valid respawn location: [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
        }
        return pos;
    }

    private int getRandomRange(int actual, int minDistance, int maxDistance) {
        return actual + (random.nextBoolean() ? -1 : 1) * (minDistance + random.nextInt(maxDistance - minDistance));
    }

}