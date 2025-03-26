package de.maxhenkel.betterrespawn.mixin;

import de.maxhenkel.betterrespawn.RespawnAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Abilities.class)
public class AbilitiesMixin implements RespawnAbilities {

    @Unique
    @Nullable
    private ServerPlayer.RespawnConfig respawnConfig;

    @Inject(method = "addSaveData", at = @At(value = "RETURN"))
    private void addSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if (respawnConfig == null) {
            return;
        }
        CompoundTag abilities = compoundTag.getCompoundOrEmpty("abilities");
        CompoundTag betterRespawn = new CompoundTag();

        betterRespawn.putString("respawn_dimension", respawnConfig.dimension().location().toString());

        CompoundTag pos = new CompoundTag();
        pos.putInt("x", respawnConfig.pos().getX());
        pos.putInt("y", respawnConfig.pos().getY());
        pos.putInt("z", respawnConfig.pos().getZ());
        betterRespawn.put("respawn_pos", pos);

        betterRespawn.putFloat("respawn_angle", respawnConfig.angle());
        betterRespawn.putBoolean("respawn_forced", respawnConfig.forced());

        abilities.put("better_respawn", betterRespawn);
    }

    @Inject(method = "loadSaveData", at = @At(value = "RETURN"))
    private void loadSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        Optional<CompoundTag> optionalAbilities = compoundTag.getCompound("abilities");
        if (optionalAbilities.isEmpty()) {
            return;
        }
        CompoundTag abilities = optionalAbilities.get();

        respawnConfig = abilities.getCompound("better_respawn").flatMap(tag -> {
            ResourceKey<Level> respawnDimension = tag.read("respawn_dimension", ResourceKey.codec(Registries.DIMENSION)).orElse(Level.OVERWORLD);
            Optional<CompoundTag> optionalRespawnPos = tag.getCompound("respawn_pos");
            if (optionalRespawnPos.isEmpty()) {
                return Optional.empty();
            }
            CompoundTag posTag = optionalRespawnPos.get();
            BlockPos respawnPos = new BlockPos(posTag.getIntOr("x", 0), posTag.getIntOr("y", 0), posTag.getIntOr("z", 0));
            float respawnAngle = tag.getFloatOr("respawn_angle", 0F);
            boolean respawnForced = tag.getBooleanOr("respawn_forced", false);
            return Optional.of(new ServerPlayer.RespawnConfig(respawnDimension, respawnPos, respawnAngle, respawnForced));
        }).orElse(null);
    }

    @Override
    public void setRespawnConfig(ServerPlayer.RespawnConfig respawnConfig) {
        this.respawnConfig = respawnConfig;
    }

    @Override
    public ServerPlayer.RespawnConfig getRespawnConfig() {
        return respawnConfig;
    }
}
