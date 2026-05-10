package com.blakebr0.ironjetpacks.compat.jei;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.crafting.JetpackDynamicRecipeManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JeiCompat implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Collect all dynamically generated shaped recipes and register them with JEI
        // so that right-clicking any jetpack / component in JEI shows its recipe.
        // List must be RecipeHolder<CraftingRecipe> to match RecipeTypes.CRAFTING's type parameter.
        List<RecipeHolder<CraftingRecipe>> crafting = new ArrayList<>();

        JetpackDynamicRecipeManager.appendRecipes((key, holder) -> {
            if (holder.value() instanceof ShapedRecipe) {
                //noinspection unchecked
                crafting.add((RecipeHolder<CraftingRecipe>) holder);
            }
        });

        registration.addRecipes(RecipeTypes.CRAFTING, crafting);
    }
}
