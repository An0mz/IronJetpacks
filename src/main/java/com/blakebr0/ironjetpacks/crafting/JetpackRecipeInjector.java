package com.blakebr0.ironjetpacks.crafting;

import com.blakebr0.ironjetpacks.mixins.RecipeManagerAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;

import java.util.ArrayList;
import java.util.List;

public class JetpackRecipeInjector {
    public static void inject(MinecraftServer server) {
        RecipeManager manager = server.getRecipeManager();
        RecipeManagerAccessor accessor = (RecipeManagerAccessor) (Object) manager;

        List<RecipeHolder<?>> allRecipes = new ArrayList<>(accessor.getRecipes().values());
        JetpackDynamicRecipeManager.appendRecipes((id, holder) -> allRecipes.add(holder));

        accessor.setRecipes(RecipeMap.create(allRecipes));
        manager.finalizeRecipeLoading(server.getWorldData().enabledFeatures());
    }
}
