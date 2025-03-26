package de.maxhenkel.betterrespawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface RespawnAbilities {

    void setRespawnConfig(@Nullable ServerPlayer.RespawnConfig respawnConfig);

    @Nullable
    ServerPlayer.RespawnConfig getRespawnConfig();

}
