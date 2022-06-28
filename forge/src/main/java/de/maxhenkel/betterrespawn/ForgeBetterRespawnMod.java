package de.maxhenkel.betterrespawn;

import de.maxhenkel.betterrespawn.config.ForgeServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Function;

@Mod(BetterRespawnMod.MODID)
public class ForgeBetterRespawnMod extends BetterRespawnMod {

    public ForgeBetterRespawnMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        init();
        SERVER_CONFIG = registerConfig(ModConfig.Type.SERVER, ForgeServerConfig::new);
    }

    public static <T> T registerConfig(ModConfig.Type type, Function<ForgeConfigSpec.Builder, T> consumer) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        T config = consumer.apply(builder);
        ForgeConfigSpec spec = builder.build();
        ModLoadingContext.get().registerConfig(type, spec);
        return config;
    }

}
