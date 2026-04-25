package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModelHandler {

    public static void onClientSetup() {
        ModelLoadingPlugin.register(context -> {
            context.resolveModel().register(ctx -> {
                if (!ctx.id().getNamespace().equals(IronJetpacks.MOD_ID)) return null;

                String path = ctx.id().getPath();

                // Check at resolve time, not setup time
                for (var j : JetpackRegistry.getInstance().getAllJetpacks()) {
                    if (path.equals("item/" + j.name + "_cell"))
                        return ctx.getOrLoadModel(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/cell"));
                    if (path.equals("item/" + j.name + "_capacitor"))
                        return ctx.getOrLoadModel(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/capacitor"));
                    if (path.equals("item/" + j.name + "_thruster"))
                        return ctx.getOrLoadModel(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/thruster"));
                    if (path.equals("item/" + j.name + "_jetpack"))
                        return ctx.getOrLoadModel(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/jetpack"));
                }

                return null;
            });
        });
    }
}
