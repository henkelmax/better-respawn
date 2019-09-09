package de.maxhenkel.better_respawn;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        Pair<ServerConfig, ForgeConfigSpec> specPairServer = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPairServer.getRight();
        SERVER = specPairServer.getLeft();
    }

    public static class ServerConfig {
        public ForgeConfigSpec.IntValue MAX_RESPAWN_DISTANCE;
        public ForgeConfigSpec.IntValue MIN_RESPAWN_DISTANCE;

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            MAX_RESPAWN_DISTANCE = builder
                    .comment("The maximum distance to spawn the player away from its death location")
                    .defineInRange("max_respawn_distance", 256, 16, 4096);

            MIN_RESPAWN_DISTANCE = builder
                    .comment("The minimum distance to spawn the player away from its death location")
                    .defineInRange("min_respawn_distance", 128, 0, 2048);
        }
    }

}
