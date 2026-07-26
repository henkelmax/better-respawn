package de.maxhenkel.betterrespawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface RespawnAbilities {

    void better_respawn$setRespawnConfig(@Nullable ServerPlayer.RespawnConfig respawnConfig);

    @Nullable
    ServerPlayer.RespawnConfig better_respawn$getRespawnConfig();

    void better_respawn$setRespawnSearch(@Nullable CompletableFuture<?> respawnSearch);

    @Nullable
    CompletableFuture<?> better_respawn$getRespawnSearch();

}
