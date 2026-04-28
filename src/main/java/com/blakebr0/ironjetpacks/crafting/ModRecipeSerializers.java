package com.blakebr0.ironjetpacks.crafting;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.crafting.recipe.JetpackUpgradeRecipe;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final List<JetpackItem> ALL_JETPACKS = new ArrayList<>();
    public static final Supplier<RecipeSerializer<JetpackUpgradeRecipe>> CRAFTING_JETPACK_UPGRADE = Suppliers.memoize(JetpackUpgradeRecipe.Serializer::new);
    
    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "crafting_jetpack_upgrade"), CRAFTING_JETPACK_UPGRADE.get());
    }
    
    public static void onCommonSetup() {
        List<JetpackItem> jetpacks = new ArrayList<>();
        BuiltInRegistries.ITEM.stream().filter(i -> i instanceof JetpackItem).forEach(i -> jetpacks.add((JetpackItem) i));
        ALL_JETPACKS.addAll(jetpacks);
    }
}
