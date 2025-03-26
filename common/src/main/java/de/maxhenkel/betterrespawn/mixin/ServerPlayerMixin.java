package de.maxhenkel.betterrespawn.mixin;

import de.maxhenkel.betterrespawn.BetterRespawnMod;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "die", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        BetterRespawnMod.RESPAWN_MANAGER.onPlayerDeath((ServerPlayer) ((Object) this));
    }

    @Inject(method = "setRespawnPosition", at = @At("HEAD"))
    public void setRespawnPosition(ServerPlayer.RespawnConfig respawnConfig, boolean showMessage, CallbackInfo ci) {
        BetterRespawnMod.RESPAWN_MANAGER.onSetRespawnPosition((ServerPlayer) ((Object) this), respawnConfig, showMessage);
    }

}
