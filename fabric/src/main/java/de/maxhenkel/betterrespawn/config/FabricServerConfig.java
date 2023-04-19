package de.maxhenkel.betterrespawn.config;

import de.maxhenkel.configbuilder.ConfigBuilder;

public class FabricServerConfig extends ServerConfig {

    public FabricServerConfig(ConfigBuilder builder) {
        maxRespawnDistance = builder.integerEntry(
                "max_respawn_distance",
                256,
                16,
                8192,
                "The maximum distance to spawn the player away from its death location"
        );
        minRespawnDistance = builder.integerEntry(
                "min_respawn_distance",
                128,
                0,
                4096,
                "The minimum distance to spawn the player away from its death location"
        );
        respawnBlockRange = builder.integerEntry(
                "respawn_block_range",
                256,
                0,
                Integer.MAX_VALUE,
                "If the player is in this range of its bed/respawn anchor it will respawn there"
        );
    }

}
