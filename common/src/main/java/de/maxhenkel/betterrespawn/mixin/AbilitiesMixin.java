package de.maxhenkel.betterrespawn.mixin;

import de.maxhenkel.betterrespawn.RespawnAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Abilities.class)
public abstract class AbilitiesMixin implements RespawnAbilities {


    /** Custom added variables for Hardcore Respawn **/
    private static final String LAST_DEATH_TIME_TAG = "last_death_time";
    private long lastDeathTime;

    /** **/

    private ResourceKey<Level> respawnDimension;
    @Nullable
    private BlockPos respawnPos;
    private float respawnAngle;
    private boolean respawnForced;

    @Inject(method = "addSaveData", at = @At(value = "RETURN"))
    private void addSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag abilities = compoundTag.getCompound("abilities");

        CompoundTag betterRespawn = new CompoundTag();

        if (respawnDimension != null) {
            betterRespawn.putString("respawn_dimension", respawnDimension.location().toString());
        }

        if (respawnPos != null) {
            CompoundTag pos = new CompoundTag();
            pos.putInt("x", respawnPos.getX());
            pos.putInt("y", respawnPos.getY());
            pos.putInt("z", respawnPos.getZ());
            betterRespawn.put("respawn_pos", pos);
        }

        betterRespawn.putFloat("respawn_angle", respawnAngle);
        betterRespawn.putBoolean("respawn_forced", respawnForced);


        abilities.put("better_respawn", betterRespawn);

        // Save the last death timestamp
        compoundTag.putLong(LAST_DEATH_TIME_TAG, lastDeathTime);



    }

    @Inject(method = "loadSaveData", at = @At(value = "RETURN"))
    private void loadSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if (!compoundTag.contains("abilities", 10)) {
            return;
        }
        CompoundTag abilities = compoundTag.getCompound("abilities");

        if (abilities.contains("better_respawn")) {
            CompoundTag betterRespawn = abilities.getCompound("better_respawn");
            if (betterRespawn.contains("respawn_dimension")) {
                respawnDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(betterRespawn.getString("respawn_level")));
            } else {
                respawnDimension = Level.OVERWORLD;
            }
            if (betterRespawn.contains("respawn_pos")) {
                CompoundTag pos = betterRespawn.getCompound("respawn_pos");
                respawnPos = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
            } else {
                respawnPos = null;
            }
            if (betterRespawn.contains("respawn_angle")) {
                respawnAngle = betterRespawn.getFloat("respawn_angle");
            } else {
                respawnAngle = 0F;
            }
            if (betterRespawn.contains("respawn_forced")) {
                respawnForced = betterRespawn.getBoolean("respawn_forced");
            } else {
                respawnForced = false;
            }
        } else {
            respawnDimension = Level.OVERWORLD;
            respawnPos = null;
            respawnAngle = 0F;
            respawnForced = false;
        }

        // Load the last death timestamp
        if (abilities.contains(LAST_DEATH_TIME_TAG)) {
            lastDeathTime = abilities.getLong(LAST_DEATH_TIME_TAG);
        } else {
            lastDeathTime = 0L;
        }

    }


    @Override
    public void setRespawnDimension(ResourceKey<Level> dimension) {
        this.respawnDimension = dimension;
    }

    @Override
    public void setRespawnPos(@Nullable BlockPos pos) {
        this.respawnPos = pos;
    }

    @Override
    public void setRespawnAngle(float angle) {
        this.respawnAngle = angle;
    }

    @Override
    public void setRespawnForced(boolean forced) {
        this.respawnForced = forced;
    }

    @Override
    public ResourceKey<Level> getRespawnDimension() {
        return respawnDimension;
    }

    @Override
    public @Nullable BlockPos getRespawnPos() {
        return respawnPos;
    }

    @Override
    public float getRespawnAngle() {
        return respawnAngle;
    }

    @Override
    public boolean getRespawnForced() {
        return respawnForced;
    }

    @Override
    public long getLastDeathTime() {
        return lastDeathTime;
    }
}