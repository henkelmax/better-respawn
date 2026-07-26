package de.maxhenkel.betterrespawn.mixin;

import de.maxhenkel.betterrespawn.BetterRespawnMod;
import de.maxhenkel.betterrespawn.RespawnAbilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "respawn", at = @At(value = "HEAD"))
    private void beforeRespawn(ServerPlayer serverPlayer, boolean bl, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
        BetterRespawnMod.RESPAWN_MANAGER.awaitRespawnSearch(serverPlayer);
    }

    @Inject(method = "respawn", at = @At(value = "RETURN"))
    private void respawn(ServerPlayer serverPlayer, boolean bl, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer newPlayer = cir.getReturnValue();

        if (!(serverPlayer instanceof RespawnAbilities abilities)) {
            return;
        }

        newPlayer.setRespawnPosition(abilities.better_respawn$getRespawnConfig(), false);
    }

}
