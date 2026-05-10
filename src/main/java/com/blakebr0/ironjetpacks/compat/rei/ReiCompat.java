package com.blakebr0.ironjetpacks.compat.rei;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.crafting.JetpackDynamicRecipeManager;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ReiCompat implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // Register all dynamically generated jetpack recipes with REI so they
        // appear when looking up any jetpack or component in the recipe viewer.
        JetpackDynamicRecipeManager.appendRecipes((key, holder) -> {
            if (holder.value() instanceof ShapedRecipe) {
                //noinspection unchecked
                registry.add(DefaultCraftingDisplay.of((RecipeHolder<ShapedRecipe>) holder));
            }
        });
    }
}
