package de.maxhenkel.better_respawn;

import de.maxhenkel.better_respawn.net.MessageRespawnDelay;
import de.maxhenkel.corelib.net.NetUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RespawnTimerEvents {

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.player.isAlive() || !(event.player instanceof ServerPlayerEntity)) {
            return;
        }
        if (Main.SERVER_CONFIG.respawnCooldown.get() <= 0) {
            return;
        }
        if (event.player.deathTime % 20 == 0) {
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, (ServerPlayerEntity) event.player, new MessageRespawnDelay(Math.max(0, Main.SERVER_CONFIG.respawnCooldown.get() - event.player.deathTime)));
        } else if (event.player.deathTime == Main.SERVER_CONFIG.respawnCooldown.get()) {
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, (ServerPlayerEntity) event.player, new MessageRespawnDelay(0));
        }
    }

}
