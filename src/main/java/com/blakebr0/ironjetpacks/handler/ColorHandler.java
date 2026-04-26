package com.blakebr0.ironjetpacks.handler;

import com.blakebr0.ironjetpacks.item.Colored;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.level.ItemLike;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ColorHandler {
    public static void onClientSetup() {
        JetpackRegistry registry = JetpackRegistry.getInstance();
        if (registry.isErrored()) return;

        List<ItemLike> items = new ArrayList<>();
        for (Jetpack jetpack : registry.getAllJetpacks()) {
            items.add(jetpack.item.get());
            items.add(jetpack.cell);
            items.add(jetpack.thruster);
            items.add(jetpack.capacitor);
        }

        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> {
                    if (stack.getItem() instanceof Colored colored) {
                        return colored.getColorTint(tintIndex);
                    }
                    return -1;
                },
                items.toArray(new ItemLike[0])
        );

    }
}