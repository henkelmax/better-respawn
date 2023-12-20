package de.maxhenkel.betterrespawn.mixin;

import de.maxhenkel.betterrespawn.RespawnAbilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "respawn", at = @At(value = "RETURN"))
    private void respawn(ServerPlayer player, boolean fromEnd, CallbackInfoReturnable<ServerPlayer> ci) {
        ServerPlayer newPlayer = ci.getReturnValue();

        if (!(player.getAbilities() instanceof RespawnAbilities abilities)) {
            return;
        }

        newPlayer.setRespawnPosition(abilities.getRespawnDimension(), abilities.getRespawnPos(), abilities.getRespawnAngle(), abilities.getRespawnForced(), false);
    }

}