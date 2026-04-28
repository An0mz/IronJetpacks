package com.blakebr0.ironjetpacks.handler;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.client.ColoredTintSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ColorHandler {
    public static void onClientSetup() {
        ItemTintSources.ID_MAPPER.put(
                Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "colored"),
            ColoredTintSource.CODEC
        );
    }
}
