package com.blakebr0.ironjetpacks.compat.jei;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class JeiCompat implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            if (!jetpack.creative) {
                registration.registerFromDataComponentTypes(
                        jetpack.item.get(),
                        DataComponents.CUSTOM_DATA
                );
            }
        }
    }
}