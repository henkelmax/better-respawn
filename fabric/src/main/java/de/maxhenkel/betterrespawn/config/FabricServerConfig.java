package de.maxhenkel.betterrespawn.config;


import de.maxhenkel.configbuilder.ConfigBuilder;

public class FabricServerConfig extends ServerConfig {

    public FabricServerConfig(ConfigBuilder builder) {
        maxRespawnDistance = builder.integerEntry(
                "max_respawn_distance",
                2500,
                16,
                8192,
                "Maximum distance for player respawn from the center of the world"
        );
        minRespawnDistance = builder.integerEntry(
                "min_respawn_distance",
                1000,
                0,
                4096,
                "Minimum distance for player respawn from the center of the world"
        );
        respawnBlockRange = builder.integerEntry(
                "respawn_block_range",
                1,
                0,
                Integer.MAX_VALUE,
                "Respawn range around bed/respawn anchor"
        );
    }

}
