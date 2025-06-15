package de.maxhenkel.betterrespawn.mixin;

import de.maxhenkel.betterrespawn.BetterRespawnMod;
import de.maxhenkel.betterrespawn.RespawnAbilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements RespawnAbilities {

    @Unique
    @Nullable
    private ServerPlayer.RespawnConfig cachedRespawnConfig;

    @Inject(method = "addAdditionalSaveData", at = @At(value = "RETURN"))
    private void addSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        if (cachedRespawnConfig == null) {
            return;
        }

        valueOutput.store("better_respawn", ServerPlayer.RespawnConfig.CODEC, cachedRespawnConfig);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "RETURN"))
    private void loadSaveData(ValueInput valueInput, CallbackInfo ci) {
        cachedRespawnConfig = valueInput.read("better_respawn", ServerPlayer.RespawnConfig.CODEC).orElse(null);
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        BetterRespawnMod.RESPAWN_MANAGER.onPlayerDeath((ServerPlayer) ((Object) this));
    }

    @Inject(method = "setRespawnPosition", at = @At("HEAD"))
    public void setRespawnPosition(ServerPlayer.RespawnConfig respawnConfig, boolean showMessage, CallbackInfo ci) {
        BetterRespawnMod.RESPAWN_MANAGER.onSetRespawnPosition((ServerPlayer) ((Object) this), respawnConfig, showMessage);
    }

    @Override
    public void better_respawn$setRespawnConfig(ServerPlayer.RespawnConfig respawnConfig) {
        this.cachedRespawnConfig = respawnConfig;
    }

    @Override
    public ServerPlayer.RespawnConfig better_respawn$getRespawnConfig() {
        return cachedRespawnConfig;
    }

}
