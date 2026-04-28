package com.blakebr0.ironjetpacks.compat.kubejs;

import com.blakebr0.ironjetpacks.crafting.JetpackDynamicRecipeManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class KubeJSCompat implements Consumer<Map<RecipeType<?>, Map<Identifier, Recipe<?>>>> {
    @Override
    public void accept(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeTypeMapMap) {
        Map<Identifier, Recipe<?>> builder = recipeTypeMapMap.computeIfAbsent(RecipeType.CRAFTING, recipeType -> new HashMap<>());
        JetpackDynamicRecipeManager.appendRecipes((id, holder) -> builder.put(id.identifier(), holder.value()));
    }
}
