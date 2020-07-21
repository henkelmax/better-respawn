package de.maxhenkel.better_respawn;

import de.maxhenkel.better_respawn.capabilities.RespawnPosition;
import de.maxhenkel.better_respawn.capabilities.SpawnPointCapabilityStorage;
import de.maxhenkel.corelib.CommonRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "better_respawn";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @CapabilityInject(RespawnPosition.class)
    public static Capability<RespawnPosition> RESPAWN_CAPABILITY;

    public static ServerConfig SERVER_CONFIG;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.SERVER, ServerConfig.class);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RespawnEvents());

        CapabilityManager.INSTANCE.register(RespawnPosition.class, new SpawnPointCapabilityStorage(), () -> new RespawnPosition());
    }

}
