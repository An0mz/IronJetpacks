package com.blakebr0.ironjetpacks.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelHandler {

    public static void onClientSetup() {
        // Item models for registered items are loaded automatically by Minecraft.
        // The Fabric API addModels(ResourceLocation) API was removed in 0.128.x.
    }
}