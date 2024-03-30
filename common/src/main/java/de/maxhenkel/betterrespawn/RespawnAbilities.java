package de.maxhenkel.betterrespawn;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface RespawnAbilities {

    void setRespawnDimension(ResourceKey<Level> dimension);

    void setRespawnPos(@Nullable BlockPos pos);

    void setRespawnAngle(float angle);

    void setRespawnForced(boolean forced);

    ResourceKey<Level> getRespawnDimension();

    @Nullable
    BlockPos getRespawnPos();

    float getRespawnAngle();

    boolean getRespawnForced();


    // Hardcore Respawn added


    long getLastDeathTime();

}