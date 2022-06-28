package de.maxhenkel.betterrespawn.config;

import de.maxhenkel.configbuilder.Config;
import de.maxhenkel.configbuilder.ConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ForgeServerConfig extends ServerConfig {

    public ForgeServerConfig(ForgeConfigSpec.Builder builder) {
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

    public static <T> ConfigEntry<T> wrapConfigEntry(ForgeConfigSpec.ConfigValue<T> configValue) {
        return new ConfigEntry<T>() {
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
                return fromBuilder(configValue.next());
            }
        };
    }

    public static Config fromBuilder(ForgeConfigSpec.Builder builder) {
        Map<String, Object> entries;
        try {
            Field field = builder.getClass().getDeclaredField("storage");
            com.electronwill.nightconfig.core.Config config = (com.electronwill.nightconfig.core.Config) field.get(builder);
            entries = config.valueMap();
        } catch (Exception e) {
            entries = new HashMap<>();
        }
        Map<String, Object> finalEntries = entries;
        return () -> finalEntries;
    }
}
