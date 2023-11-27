package de.maxhenkel.betterrespawn.config;

import de.maxhenkel.configbuilder.Config;
import de.maxhenkel.configbuilder.ConfigEntry;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ForgeServerConfig extends ServerConfig {

    public ForgeServerConfig(ModConfigSpec.Builder builder) {
        maxRespawnDistance = wrapConfigEntry(builder
                .comment("The maximum distance to spawn the player away from its death location")
                .defineInRange("max_respawn_distance", 256, 16, 8192));
        minRespawnDistance = wrapConfigEntry(builder
                .comment("The minimum distance to spawn the player away from its death location")
                .defineInRange("min_respawn_distance", 128, 0, 4096));
        respawnBlockRange = wrapConfigEntry(builder
                .comment("If the player is in this range of its bed/respawn anchor it will respawn there")
                .defineInRange("respawn_block_range", 256, 0, Integer.MAX_VALUE));
    }

    public static <T> ConfigEntry<T> wrapConfigEntry(ModConfigSpec.ConfigValue<T> configValue) {
        return new ConfigEntry<T>() {
            @Override
            public ConfigEntry<T> comment(String... comments) {
                return this;
            }

            @Override
            public String[] getComments() {
                return new String[0];
            }

            @Override
            public T get() {
                return configValue.get();
            }

            @Override
            public ConfigEntry<T> set(T t) {
                configValue.set(t);
                return this;
            }

            @Override
            public String getKey() {
                throw new UnsupportedOperationException("Can't get key of Forge config value");
            }

            @Override
            public ConfigEntry<T> reset() {
                throw new UnsupportedOperationException("Can't reset Forge config value");
            }

            @Override
            public ConfigEntry<T> save() {
                configValue.save();
                return this;
            }

            @Override
            public ConfigEntry<T> saveSync() {
                throw new UnsupportedOperationException("Can't synchronously save Forge config value");
            }

            @Override
            public T getDefault() {
                throw new UnsupportedOperationException("Cannot get default config value");
            }

            @Override
            public Config getConfig() {
                throw new UnsupportedOperationException("Cannot get config of Forge config value");
            }
        };
    }

}
