package de.maxhenkel.better_respawn;

import de.maxhenkel.better_respawn.capabilities.RespawnPosition;
import de.maxhenkel.better_respawn.net.MessageRespawnDelay;
import de.maxhenkel.corelib.CommonRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "better_respawn";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static Capability<RespawnPosition> RESPAWN_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static SimpleChannel SIMPLE_CHANNEL;

    public static ServerConfig SERVER_CONFIG;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterCapabilities);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.SERVER, ServerConfig.class);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RespawnEvents());
        MinecraftForge.EVENT_BUS.register(new RespawnTimerEvents());

        SIMPLE_CHANNEL = CommonRegistry.registerChannel(Main.MODID, "default");
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 0, MessageRespawnDelay.class);
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(RespawnPosition.class);
    }

}
