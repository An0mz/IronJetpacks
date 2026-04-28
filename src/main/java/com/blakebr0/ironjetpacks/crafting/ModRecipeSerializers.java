package com.blakebr0.ironjetpacks.crafting;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.crafting.recipe.JetpackUpgradeRecipe;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;

public class ModRecipeSerializers {
    public static final List<JetpackItem> ALL_JETPACKS = new ArrayList<>();
    public static final RecipeSerializer<JetpackUpgradeRecipe> CRAFTING_JETPACK_UPGRADE = JetpackUpgradeRecipe.SERIALIZER;

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "crafting_jetpack_upgrade"), CRAFTING_JETPACK_UPGRADE);
    }
    
    public static void onCommonSetup() {
        List<JetpackItem> jetpacks = new ArrayList<>();
        BuiltInRegistries.ITEM.stream().filter(i -> i instanceof JetpackItem).forEach(i -> jetpacks.add((JetpackItem) i));
        ALL_JETPACKS.addAll(jetpacks);
    }
}
