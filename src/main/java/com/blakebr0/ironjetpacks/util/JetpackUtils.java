package com.blakebr0.ironjetpacks.util;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.EnumMap;
import java.util.Map;

public class JetpackUtils {
    public static boolean isFlying(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof JetpackItem jetpack) {
                Jetpack info = jetpack.getJetpack();
                boolean hasEnergy = info.creative || player.isCreative()
                        || SimpleEnergyItem.getStoredEnergyUnchecked(stack) >= (long) info.usage;
                if (jetpack.isEngineOn(stack) && hasEnergy) {
                    if (jetpack.isHovering(stack)) {
                        return !player.onGround();
                    } else {
                        return InputHandler.isHoldingUp(player);
                    }
                }
            }
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
                TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "repairs_jetpack")),
                EquipmentAssets.LEATHER  // placeholder, jetpack uses custom rendering anyway
        );
    }
}