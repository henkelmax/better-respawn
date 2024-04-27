package de.maxhenkel.betterrespawn;

import de.maxhenkel.betterrespawn.config.ForgeServerConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Function;

@Mod(BetterRespawnMod.MODID)
public class ForgeBetterRespawnMod extends BetterRespawnMod {

    public ForgeBetterRespawnMod(IEventBus eventBus) {
        eventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        init();
        SERVER_CONFIG = registerConfig(ModConfig.Type.SERVER, ForgeServerConfig::new);
    }

    public static <T> T registerConfig(ModConfig.Type type, Function<ModConfigSpec.Builder, T> consumer) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        T config = consumer.apply(builder);
        ModConfigSpec spec = builder.build();
        ModContainer modContainer = ModList.get().getModContainerById(MODID).orElseThrow(() -> new RuntimeException("Could not find mod %s".formatted(MODID)));
        modContainer.registerConfig(type, spec);
        return config;
    }

}
