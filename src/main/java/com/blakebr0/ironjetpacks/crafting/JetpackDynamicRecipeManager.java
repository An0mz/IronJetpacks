package com.blakebr0.ironjetpacks.crafting;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.crafting.recipe.JetpackUpgradeRecipe;
import com.blakebr0.ironjetpacks.item.ModItems;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;


public class JetpackDynamicRecipeManager {
    public static void appendRecipes(BiConsumer<ResourceKey<Recipe<?>>, RecipeHolder<?>> appender) {
        JetpackRegistry.getInstance().getAllJetpacks().forEach(jetpack -> {
            Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> cell = makeCellRecipe(jetpack);
            Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> thruster = makeThrusterRecipe(jetpack);
            Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> capacitor = makeCapacitorRecipe(jetpack);
            Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> jetpackSelf = makeJetpackRecipe(jetpack);
            Map.Entry<ResourceKey<Recipe<?>>, JetpackUpgradeRecipe> jetpackUpgrade = makeJetpackUpgradeRecipe(jetpack);

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

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, path));
    }

    private static ShapedRecipePattern toPattern(int width, int height, List<Optional<Ingredient>> inputs) {
        return new ShapedRecipePattern(width, height, inputs, Optional.empty());
    }

    private static Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> makeCellRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableCellRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();

        Ingredient material = jetpack.getCraftingMaterial();
        Item coilItem = jetpacks.getCoilForTier(jetpack.tier);
        if (material == null || coilItem == null)
            return null;

        Ingredient coil = Ingredient.of(coilItem);
        Ingredient redstone = Ingredient.of(Items.REDSTONE);
        List<Optional<Ingredient>> inputs = List.of(
                Optional.empty(),          Optional.of(redstone), Optional.empty(),
                Optional.of(material),     Optional.of(coil),     Optional.of(material),
                Optional.empty(),          Optional.of(redstone), Optional.empty()
        );

        ResourceKey<Recipe<?>> key = recipeKey(jetpack.name + "_cell");
        ItemStack output = new ItemStack(jetpack.cell);
        return Map.entry(key, new ShapedRecipe("iron-jetpacks:cells", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> makeThrusterRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableThrusterRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();

        Ingredient material = jetpack.getCraftingMaterial();
        Item coilItem = jetpacks.getCoilForTier(jetpack.tier);
        if (material == null || coilItem == null)
            return null;

        Ingredient coil = Ingredient.of(coilItem);
        Ingredient cell = Ingredient.of(jetpack.cell);
        Ingredient furnace = Ingredient.of(Blocks.FURNACE);
        List<Optional<Ingredient>> inputs = List.of(
                Optional.of(material), Optional.of(coil),    Optional.of(material),
                Optional.of(coil),     Optional.of(cell),    Optional.of(coil),
                Optional.of(material), Optional.of(furnace), Optional.of(material)
        );

        ResourceKey<Recipe<?>> key = recipeKey(jetpack.name + "_thruster");
        ItemStack output = new ItemStack(jetpack.thruster);
        return Map.entry(key, new ShapedRecipe("iron-jetpacks:thrusters", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> makeCapacitorRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableCapacitorRecipes)
            return null;

        Ingredient material = jetpack.getCraftingMaterial();
        if (material == null)
            return null;

        Ingredient cell = Ingredient.of(jetpack.cell);
        List<Optional<Ingredient>> inputs = List.of(
                Optional.of(material), Optional.of(cell), Optional.of(material),
                Optional.of(material), Optional.of(cell), Optional.of(material),
                Optional.of(material), Optional.of(cell), Optional.of(material)
        );

        ResourceKey<Recipe<?>> key = recipeKey(jetpack.name + "_capacitor");
        ItemStack output = new ItemStack(jetpack.capacitor);
        return Map.entry(key, new ShapedRecipe("iron-jetpacks:capacitors", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceKey<Recipe<?>>, ShapedRecipe> makeJetpackRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableJetpackRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        if (jetpack.tier != jetpacks.getLowestTier())
            return null;

        Ingredient material = jetpack.getCraftingMaterial();
        if (material == null)
            return null;

        Ingredient capacitor = Ingredient.of(jetpack.capacitor);
        Ingredient thruster = Ingredient.of(jetpack.thruster);
        Ingredient strap = Ingredient.of(ModItems.STRAP.get());
        List<Optional<Ingredient>> inputs = List.of(
                Optional.of(material),  Optional.of(capacitor), Optional.of(material),
                Optional.of(material),  Optional.of(strap),     Optional.of(material),
                Optional.of(thruster),  Optional.empty(),        Optional.of(thruster)
        );

        ResourceKey<Recipe<?>> key = recipeKey(jetpack.name + "_jetpack");
        ItemStack output = new ItemStack(jetpack.item.get());
        return Map.entry(key, new ShapedRecipe("iron-jetpacks:jetpacks", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }

    private static Map.Entry<ResourceKey<Recipe<?>>, JetpackUpgradeRecipe> makeJetpackUpgradeRecipe(Jetpack jetpack) {
        if (!ModConfigs.get().recipe.enableJetpackRecipes)
            return null;

        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        if (jetpack.tier == jetpacks.getLowestTier())
            return null;

        Ingredient material = jetpack.getCraftingMaterial();
        if (material == null)
            return null;

        Ingredient capacitor = Ingredient.of(jetpack.capacitor);
        Ingredient thruster = Ingredient.of(jetpack.thruster);
        Ingredient jetpackTier = Ingredient.of(ModRecipeSerializers.ALL_JETPACKS.stream()
                .filter(item -> item.getJetpack().tier == jetpack.tier - 1)
                .toArray(ItemLike[]::new));
        List<Optional<Ingredient>> inputs = List.of(
                Optional.of(material),  Optional.of(capacitor),   Optional.of(material),
                Optional.of(material),  Optional.of(jetpackTier), Optional.of(material),
                Optional.of(thruster),  Optional.empty(),          Optional.of(thruster)
        );

        ResourceKey<Recipe<?>> key = recipeKey(jetpack.name + "_jetpack");
        ItemStack output = new ItemStack(jetpack.item.get());
        return Map.entry(key, new JetpackUpgradeRecipe("iron-jetpacks:jetpacks", CraftingBookCategory.MISC, toPattern(3, 3, inputs), output));
    }
}
