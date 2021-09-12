package de.maxhenkel.better_respawn.mixins;

import de.maxhenkel.better_respawn.Main;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetHandlerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleClientCommand", at = @At("HEAD"), cancellable = true)
    public void processClientStatus(ServerboundClientCommandPacket packet, CallbackInfo ci) {
        if (packet.getAction().equals(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)) {
            if (player.deathTime < Main.SERVER_CONFIG.respawnCooldown.get()) {
                ci.cancel();
            }
        }
    }

}
