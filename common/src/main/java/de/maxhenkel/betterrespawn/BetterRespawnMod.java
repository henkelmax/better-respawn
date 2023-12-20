package de.maxhenkel.betterrespawn;

import de.maxhenkel.betterrespawn.config.ServerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BetterRespawnMod {

    public static final String MODID = "better_respawn";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ServerConfig SERVER_CONFIG;
    public static RespawnManager RESPAWN_MANAGER;

    public void init() {
        RESPAWN_MANAGER = new RespawnManager();


    }

}