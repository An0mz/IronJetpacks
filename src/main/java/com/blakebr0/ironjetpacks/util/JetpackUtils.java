package com.blakebr0.ironjetpacks.util;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.compat.trinkets.TrinketsCompat;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class JetpackUtils {
    public static ItemStack getJetpackStack(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.isEmpty() && chest.getItem() instanceof JetpackItem) {
            return chest;
        }
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            return TrinketsCompat.getEquippedJetpackStack(player);
        }
        return ItemStack.EMPTY;
    }

    public static void withJetpack(Player player, BiConsumer<ItemStack, Runnable> action) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.isEmpty() && chest.getItem() instanceof JetpackItem) {
            action.accept(chest, () -> {});
            return;
        }
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsCompat.withJetpack(player, action);
        }
    }

    public static boolean isFlying(Player player) {
        ItemStack stack = getJetpackStack(player);
        if (!stack.isEmpty() && stack.getItem() instanceof JetpackItem jetpack) {
            Jetpack info = jetpack.getJetpack();
            boolean hasEnergy = info.creative || player.isCreative();
            if (!hasEnergy) {
                hasEnergy = team.reborn.energy.api.base.SimpleEnergyItem.getStoredEnergyUnchecked(stack) >= (long) info.usage;
            }
            if (jetpack.isEngineOn(stack) && hasEnergy) {
                return jetpack.isHovering(stack) ? !player.onGround() : InputHandler.isHoldingUp(player);
            }
        }
        return false;
    }

    public static ArmorMaterial makeArmorMaterial(Jetpack jetpack) {
        Map<ArmorItem.Type, Integer> defenses = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            defenses.put(type, type == ArmorItem.Type.CHESTPLATE ? jetpack.armorPoints : 0);
        }
        return new ArmorMaterial(
            defenses,
            jetpack.enchantablilty,
            SoundEvents.ARMOR_EQUIP_GENERIC,
                () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "jetpack"))),
            0.0f,
            0.0f
        );
    }
}
