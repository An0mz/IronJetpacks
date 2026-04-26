package com.blakebr0.ironjetpacks.mixins;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor("recipes")
    RecipeMap getRecipes();

    @Accessor("recipes")
    void setRecipes(RecipeMap recipes);
}
