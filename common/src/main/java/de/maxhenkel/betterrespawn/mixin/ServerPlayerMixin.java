package de.maxhenkel.betterrespawn.mixin;

import de.maxhenkel.betterrespawn.BetterRespawnMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "die", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        BetterRespawnMod.RESPAWN_MANAGER.onPlayerDeath((ServerPlayer) ((Object) this));
    }

    @Inject(method = "setRespawnPosition", at = @At("HEAD"))
    public void setRespawnPosition(ResourceKey<Level> dimension, @Nullable BlockPos pos, float angle, boolean forced, boolean showMessage, CallbackInfo ci) {
        BetterRespawnMod.RESPAWN_MANAGER.onSetRespawnPosition((ServerPlayer) ((Object) this), dimension, pos, angle, forced, showMessage);
    }

}