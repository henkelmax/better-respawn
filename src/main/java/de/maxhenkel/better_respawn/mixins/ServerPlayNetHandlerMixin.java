package de.maxhenkel.better_respawn.mixins;

import de.maxhenkel.better_respawn.Main;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CClientStatusPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetHandler.class)
public class ServerPlayNetHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "handleClientCommand", at = @At("HEAD"), cancellable = true)
    public void processClientStatus(CClientStatusPacket packet, CallbackInfo ci) {
        if (packet.getAction().equals(CClientStatusPacket.State.PERFORM_RESPAWN)) {
            if (player.deathTime < Main.SERVER_CONFIG.respawnCooldown.get()) {
                ci.cancel();
            }
        }
    }

}
