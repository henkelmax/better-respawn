package de.maxhenkel.betterrespawn.util;

import net.minecraft.resources.ResourceLocation;

public class NetworkUtil {

    public static ResourceLocation id(String modId, String name) {
        return new ResourceLocation(modId, name);
    }
}