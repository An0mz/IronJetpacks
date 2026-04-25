package com.blakebr0.ironjetpacks.crafting;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.crafting.recipe.JetpackUpgradeRecipe;
import com.blakebr0.ironjetpacks.item.ModItems;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;


public class JetpackDynamicRecipeManager {
    public static void appendRecipes(BiConsumer<ResourceLocation, RecipeHolder<?>> appender) {
        JetpackRegistry.getInstance().getAllJetpacks().forEach(jetpack -> {
            Map.Entry<ResourceLocation, ShapedRecipe> cell = makeCellRecipe(jetpack);
            Map.Entry<ResourceLocation, ShapedRecipe> thruster = makeThrusterRecipe(jetpack);
            Map.Entry<ResourceLocation, ShapedRecipe> capacitor = makeCapacitorRecipe(jetpack);
            Map.Entry<ResourceLocation, ShapedRecipe> jetpackSelf = makeJetpackRecipe(jetpack);
            Map.Entry<ResourceLocation, JetpackUpgradeRecipe> jetpackUpgrade = makeJetpackUpgradeRecipe(jetpack);

            if (cell != null)
                appender.accept(cell.getKey(), new RecipeHolder<>(cell.getKey(), cell.getValue()));
            if (thruster != null)
                appender.accept(thruster.getKey(), new RecipeHolder<>(thruster.getKey(), thruster.getValue()));
            if (capacitor != null)
                appender.accept(capacitor.getKey(), new RecipeHolder<>(capacitor.getKey(), capacitor.getValue()));
            if (jetpackSelf != null)
                appender.accept(jetpackSelf.getKey(), new RecipeHolder<>(jetpackSelf.getKey(), jetpackSelf.getValue()));
            if (jetpackUpgrade != null)
                appender.accept(jetpackUpgrade.getKey(), new RecipeHolder<>(jetpackUpgrade.getKey(), jetpackUpgrade.getValue()));
        });
    }

    private static ShapedRecipePattern toPattern(int width, int height, NonNullList<Ingredient> inputs) {
        return new ShapedRecipePattern(width, height, inputs, Optional.empty());
    }

    private static Map.Entry<ResourceLocation, ShapedRecipe> makeCellRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableCellRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();

        Ingredient material = jetpack.getCraftingMaterial();
        Item coilItem = jetpacks.getCoilForTier(jetpack.tier);
        if (material == Ingredient.EMPTY || coilItem == null)
            return null;

        Ingredient coil = Ingredient.of(coilItem);
        Ingredient redstone = Ingredient.of(Items.REDSTONE);
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                Ingredient.EMPTY, redstone, Ingredient.EMPTY,
                material, coil, material,
                Ingredient.EMPTY, redstone, Ingredient.EMPTY
        );

        ResourceLocation name = ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_cell");
        ItemStack output = new ItemStack(jetpack.cell);
        return Map.entry(name, new ShapedRecipe("iron-jetpacks:cells", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceLocation, ShapedRecipe> makeThrusterRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableThrusterRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();

        Ingredient material = jetpack.getCraftingMaterial();
        Item coilItem = jetpacks.getCoilForTier(jetpack.tier);
        if (material == Ingredient.EMPTY || coilItem == null)
            return null;

        Ingredient coil = Ingredient.of(coilItem);
        Ingredient cell = Ingredient.of(jetpack.cell);
        Ingredient furnace = Ingredient.of(Blocks.FURNACE);
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                material, coil, material,
                coil, cell, coil,
                material, furnace, material
        );

        ResourceLocation name = ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_thruster");
        ItemStack output = new ItemStack(jetpack.thruster);
        return Map.entry(name, new ShapedRecipe("iron-jetpacks:thrusters", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceLocation, ShapedRecipe> makeCapacitorRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableCapacitorRecipes)
            return null;

        Ingredient material = jetpack.getCraftingMaterial();
        if (material == Ingredient.EMPTY)
            return null;

        Ingredient cell = Ingredient.of(jetpack.cell);
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                material, cell, material,
                material, cell, material,
                material, cell, material
        );

        ResourceLocation name = ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_capacitor");
        ItemStack output = new ItemStack(jetpack.capacitor);
        return Map.entry(name, new ShapedRecipe("iron-jetpacks:capacitors", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceLocation, ShapedRecipe> makeJetpackRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableJetpackRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        if (jetpack.tier != jetpacks.getLowestTier())
            return null;

        Ingredient material = jetpack.getCraftingMaterial();
        if (material == Ingredient.EMPTY)
            return null;

        Ingredient capacitor = Ingredient.of(jetpack.capacitor);
        Ingredient thruster = Ingredient.of(jetpack.thruster);
        Ingredient strap = Ingredient.of(ModItems.STRAP.get());
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                material, capacitor, material,
                material, strap, material,
                thruster, Ingredient.EMPTY, thruster
        );

        ResourceLocation name = ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_jetpack");
        ItemStack output = new ItemStack(jetpack.item.get());
        return Map.entry(name, new ShapedRecipe("iron-jetpacks:jetpacks", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceLocation, JetpackUpgradeRecipe> makeJetpackUpgradeRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableJetpackRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        if (jetpack.tier == jetpacks.getLowestTier())
            return null;

        Ingredient material = jetpack.getCraftingMaterial();
        if (material == Ingredient.EMPTY)
            return null;

        Ingredient capacitor = Ingredient.of(jetpack.capacitor);
        Ingredient thruster = Ingredient.of(jetpack.thruster);
        Ingredient jetpackTier = Ingredient.of(ModRecipeSerializers.ALL_JETPACKS.stream()
                .filter(item -> item.getJetpack().tier == jetpack.tier - 1)
                .toArray(ItemLike[]::new));
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                material, capacitor, material,
                material, jetpackTier, material,
                thruster, Ingredient.EMPTY, thruster
        );

        ResourceLocation name = ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_jetpack");
        ItemStack output = new ItemStack(jetpack.item.get());
        return Map.entry(name, new JetpackUpgradeRecipe("iron-jetpacks:jetpacks", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }
}
