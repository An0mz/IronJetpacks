package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModelHandler {

    public static void onClientSetup() {
        ModelLoadingPlugin.register(context -> {
            List<ResourceLocation> ids = new ArrayList<>();
            for (var j : JetpackRegistry.getInstance().getAllJetpacks()) {
                ids.add(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + j.name + "_cell"));
                ids.add(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + j.name + "_capacitor"));
                ids.add(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + j.name + "_thruster"));
                ids.add(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + j.name + "_jetpack"));
            }
            context.addModels(ids);
        });
    }
}