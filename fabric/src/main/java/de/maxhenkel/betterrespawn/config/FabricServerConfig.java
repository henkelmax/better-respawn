package de.maxhenkel.betterrespawn.config;

import de.maxhenkel.configbuilder.ConfigBuilder;

public class FabricServerConfig extends ServerConfig {

    public FabricServerConfig(ConfigBuilder builder) {
        maxRespawnDistance = builder.integerEntry("max_respawn_distance", 256, 16, 8192);
        minRespawnDistance = builder.integerEntry("min_respawn_distance", 128, 0, 4096);
        respawnBlockRange = builder.integerEntry("respawn_block_range", 256, 0, Integer.MAX_VALUE);
    }

}
