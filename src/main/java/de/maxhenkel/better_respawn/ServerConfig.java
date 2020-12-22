package de.maxhenkel.better_respawn;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig extends ConfigBase {

    public final ForgeConfigSpec.IntValue maxRespawnDistance;
    public final ForgeConfigSpec.IntValue minRespawnDistance;
    public final ForgeConfigSpec.IntValue bedRange;
    public final ForgeConfigSpec.IntValue respawnCooldown;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        maxRespawnDistance = builder
                .comment("The maximum distance to spawn the player away from its death location")
                .defineInRange("max_respawn_distance", 256, 16, 4096);
        minRespawnDistance = builder
                .comment("The minimum distance to spawn the player away from its death location")
                .defineInRange("min_respawn_distance", 128, 0, 2048);
        bedRange = builder
                .comment("If the player is in this range of its bed/respawn anchor it will respawn there")
                .defineInRange("bed_range", 256, 0, Integer.MAX_VALUE); //TODO rename

        respawnCooldown = builder
                .comment("The time in ticks, the player needs to wait to respawn")
                .defineInRange("respawn_cooldown", 0, 0, Integer.MAX_VALUE);
    }

}
