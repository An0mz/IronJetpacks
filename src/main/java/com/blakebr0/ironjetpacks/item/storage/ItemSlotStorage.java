package com.blakebr0.ironjetpacks.item.storage;

import com.blakebr0.ironjetpacks.mixins.LivingEntityEquipmentAccessor;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemSlotStorage extends SingleStackStorage {
    private final LivingEntity entity;
    private final EquipmentSlot slot;

    public ItemSlotStorage(LivingEntity entity, EquipmentSlot slot) {
        this.entity = entity;
        this.slot = slot;
    }

    @Override
    public ItemStack getStack() {
        return entity.getItemBySlot(slot);
    }

    @Override
    protected void setStack(ItemStack stack) {
        if (entity instanceof Player player && isArmorSlot()) {
            // Directly update the armor list to avoid triggering the equip sound,
            // which fires every tick when the energy component changes.
            ((LivingEntityEquipmentAccessor) player).getEquipment().set(slot, stack);
            // Manually sync the updated stack to the client.
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEquipmentPacket(
                    serverPlayer.getId(), List.of(Pair.of(slot, stack))
                ));
            }
        } else {
            entity.setItemSlot(slot, stack);
        }
    }

    private boolean isArmorSlot() {
        return slot == EquipmentSlot.HEAD
            || slot == EquipmentSlot.CHEST
            || slot == EquipmentSlot.LEGS
            || slot == EquipmentSlot.FEET;
    }
}
