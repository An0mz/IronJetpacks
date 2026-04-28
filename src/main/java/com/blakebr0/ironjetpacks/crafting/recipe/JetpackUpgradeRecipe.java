package com.blakebr0.ironjetpacks.crafting.recipe;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.mixins.ShapedRecipeAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class JetpackUpgradeRecipe extends ShapedRecipe {
    public static final RecipeSerializer<JetpackUpgradeRecipe> SERIALIZER = new RecipeSerializer<>(
        ShapedRecipe.MAP_CODEC.xmap(
            shaped -> new JetpackUpgradeRecipe(
                new Recipe.CommonInfo(shaped.showNotification()),
                new CraftingRecipe.CraftingBookInfo(shaped.category(), shaped.group()),
                ((ShapedRecipeAccessor)(Object) shaped).getPattern(),
                ((ShapedRecipeAccessor)(Object) shaped).getResult()),
            recipe -> recipe
        ),
        ShapedRecipe.STREAM_CODEC.map(
            shaped -> new JetpackUpgradeRecipe(
                new Recipe.CommonInfo(shaped.showNotification()),
                new CraftingRecipe.CraftingBookInfo(shaped.category(), shaped.group()),
                ((ShapedRecipeAccessor)(Object) shaped).getPattern(),
                ((ShapedRecipeAccessor)(Object) shaped).getResult()),
            recipe -> recipe
        )
    );

    private final ItemStackTemplate output;

    public JetpackUpgradeRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate output) {
        super(commonInfo, bookInfo, pattern, output);
        this.output = output;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack jetpack = input.getItem(4);
        ItemStack result = this.output.create();

        if (!jetpack.isEmpty() && jetpack.getItem() instanceof JetpackItem) {
            CustomData customData = jetpack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                result.set(DataComponents.CUSTOM_DATA, customData);
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        return (RecipeSerializer<ShapedRecipe>)(RecipeSerializer<?>) SERIALIZER;
    }
}
