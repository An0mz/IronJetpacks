package com.blakebr0.ironjetpacks.compat.jei;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.crafting.JetpackDynamicRecipeManager;
import com.blakebr0.ironjetpacks.mixins.ShapedRecipeAccessor;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class JeiCompat implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // Normalize all energy levels to the same subtype so recipe lookup works
        // regardless of charge level shown in the item panel.
        ISubtypeInterpreter<ItemStack> ignoreEnergy = (stack, context) -> "jetpack";
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            if (!jetpack.creative) {
                registration.registerSubtypeInterpreter(jetpack.item.get(), ignoreEnergy);
            }
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        // Called after the server has joined and injected dynamic recipes, so
        // appendRecipes returns the full set including all upgrade recipes.
        List<RecipeHolder<CraftingRecipe>> crafting = new ArrayList<>();

        JetpackDynamicRecipeManager.appendRecipes((key, holder) -> {
            if (holder.value() instanceof ShapedRecipe shaped) {
                Identifier baseId = key.identifier();
                ResourceKey<Recipe<?>> jeiKey = ResourceKey.create(
                        Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(baseId.getNamespace(), baseId.getPath() + "_jei")
                );
                ShapedRecipe display = new ShapedRecipe(
                        shaped.group(),
                        shaped.category(),
                        ((ShapedRecipeAccessor) (Object) shaped).getPattern(),
                        ((ShapedRecipeAccessor) (Object) shaped).getResult()
                );
                crafting.add(new RecipeHolder<>(jeiKey, display));
            }
        });

        if (!crafting.isEmpty()) {
            runtime.getRecipeManager().addRecipes(RecipeTypes.CRAFTING, crafting);
        }
    }
}
