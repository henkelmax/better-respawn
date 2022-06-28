package de.maxhenkel.betterrespawn;

import de.maxhenkel.betterrespawn.config.FabricServerConfig;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricBetterRespawnMod extends BetterRespawnMod implements ModInitializer {

    @Override
    public void onInitialize() {
        init();

        SERVER_CONFIG = ConfigBuilder.build(FabricLoader.getInstance().getConfigDir().resolve(MODID).resolve("%s.properties".formatted(MODID)), true, FabricServerConfig::new);
    }

}
