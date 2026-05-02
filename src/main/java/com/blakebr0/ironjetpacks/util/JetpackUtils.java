package com.blakebr0.ironjetpacks.util;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.compat.trinkets.TrinketsCompat;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.EnumMap;
import java.util.Map;

public class JetpackUtils {
    public static boolean isFlying(Player player) {
        // Check armor (chest equipment) slot
        ItemStack armorStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!armorStack.isEmpty() && armorStack.getItem() instanceof JetpackItem jetpack) {
            if (checkFlying(jetpack, armorStack, player)) return true;
        }

        // Check trinket slots (chest/back) — only when Trinkets mod is present
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            if (TrinketsCompat.isFlying(player)) return true;
        }

        return false;
    }

    /**
     * Returns the first active JetpackItem stack worn by the player —
     * checks the armor chest slot first, then trinket slots.
     * Returns {@link ItemStack#EMPTY} if no jetpack is equipped anywhere.
     */
    public static ItemStack getActiveJetpackStack(Player player) {
        ItemStack armorStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!armorStack.isEmpty() && armorStack.getItem() instanceof JetpackItem) {
            return armorStack;
        }
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            ItemStack trinketStack = TrinketsCompat.getEquippedJetpackStack(player);
            if (!trinketStack.isEmpty()) return trinketStack;
        }
        return ItemStack.EMPTY;
    }

    static boolean checkFlying(JetpackItem jetpack, ItemStack stack, Player player) {
        Jetpack info = jetpack.getJetpack();
        boolean hasEnergy = info.creative || player.isCreative()
                || SimpleEnergyItem.getStoredEnergyUnchecked(stack) >= (long) info.usage;
        if (jetpack.isEngineOn(stack) && hasEnergy) {
            return jetpack.isHovering(stack) ? !player.onGround() : InputHandler.isHoldingUp(player);
        }
        return false;
    }

    public static ArmorMaterial makeArmorMaterial(Jetpack jetpack) {
        Map<ArmorType, Integer> defenses = new EnumMap<>(ArmorType.class);
        for (ArmorType type : ArmorType.values()) {
            defenses.put(type, type == ArmorType.CHESTPLATE ? jetpack.armorPoints : 0);
        }
        return new ArmorMaterial(
            0,
            defenses,
            Math.max(1, jetpack.enchantablilty),
            SoundEvents.ARMOR_EQUIP_GENERIC,
            0.0f,
            0.0f,
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "repairs_jetpack")),
            ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "jetpack")
        );
    }
}