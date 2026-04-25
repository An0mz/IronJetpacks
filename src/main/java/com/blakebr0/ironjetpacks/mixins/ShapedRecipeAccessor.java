package com.blakebr0.ironjetpacks.mixins;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
    @Accessor("group")
    String getGroup();

    @Accessor("result")
    ItemStack getResult();

    @Accessor("pattern")
    ShapedRecipePattern getPattern();
}
