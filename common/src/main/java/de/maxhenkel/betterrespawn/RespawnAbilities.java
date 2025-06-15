package de.maxhenkel.betterrespawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface RespawnAbilities {

    void better_respawn$setRespawnConfig(@Nullable ServerPlayer.RespawnConfig respawnConfig);

    @Nullable
    ServerPlayer.RespawnConfig better_respawn$getRespawnConfig();

}
