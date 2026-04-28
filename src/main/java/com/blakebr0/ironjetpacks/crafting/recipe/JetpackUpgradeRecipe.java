package com.blakebr0.ironjetpacks.crafting.recipe;

import com.blakebr0.ironjetpacks.crafting.ModRecipeSerializers;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.mixins.ShapedRecipeAccessor;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class JetpackUpgradeRecipe extends ShapedRecipe {
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
    public RecipeSerializer<JetpackUpgradeRecipe> getSerializer() {
        return ModRecipeSerializers.CRAFTING_JETPACK_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<JetpackUpgradeRecipe> {
        @Override
        public MapCodec<JetpackUpgradeRecipe> codec() {
            return RecipeSerializer.SHAPED_RECIPE.codec().xmap(
                shaped -> new JetpackUpgradeRecipe(
                    new Recipe.CommonInfo(shaped.showNotification()),
                    new CraftingRecipe.CraftingBookInfo(shaped.category(), shaped.group()),
                    ((ShapedRecipeAccessor)(Object) shaped).getPattern(),
                    ((ShapedRecipeAccessor)(Object) shaped).getResult()),
                recipe -> recipe
            );
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, JetpackUpgradeRecipe> streamCodec() {
            return RecipeSerializer.SHAPED_RECIPE.streamCodec().map(
                shaped -> new JetpackUpgradeRecipe(
                    new Recipe.CommonInfo(shaped.showNotification()),
                    new CraftingRecipe.CraftingBookInfo(shaped.category(), shaped.group()),
                    ((ShapedRecipeAccessor)(Object) shaped).getPattern(),
                    ((ShapedRecipeAccessor)(Object) shaped).getResult()),
                recipe -> recipe
            );
        }
    }
}
