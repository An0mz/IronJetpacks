package com.blakebr0.ironjetpacks.crafting;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class JetpackRecipeInjector {
    public static void inject(MinecraftServer server) {
        RecipeManager manager = server.getRecipeManager();

        // Get all existing recipes
        List<RecipeHolder<?>> recipes = new ArrayList<>(manager.getOrderedRecipes());

        // Append jetpack recipes
        JetpackDynamicRecipeManager.appendRecipes((id, holder) -> recipes.add(holder));

        // Put them all back
        manager.replaceRecipes(recipes);
    }
}