package de.maxhenkel.betterrespawn.config;

import de.maxhenkel.configbuilder.entry.ConfigEntry;

public abstract class ServerConfig {

    public ConfigEntry<Integer> maxRespawnDistance;
    public ConfigEntry<Integer> minRespawnDistance;
    public ConfigEntry<Integer> respawnBlockRange;

}
